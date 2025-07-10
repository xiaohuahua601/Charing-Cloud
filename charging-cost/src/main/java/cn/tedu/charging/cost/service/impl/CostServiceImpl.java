package cn.tedu.charging.cost.service.impl;

import cn.tedu.charging.cost.dao.repository.ChargingDataCacheRepository;
import cn.tedu.charging.cost.dao.repository.CostRuleCacheRepository;
import cn.tedu.charging.cost.dao.repository.CostRuleRepository;
import cn.tedu.charging.common.pojo.param.ChargingProcessParam;
import cn.tedu.charging.cost.pojo.po.ChargingData;
import cn.tedu.charging.cost.pojo.po.CostRulePO;
import cn.tedu.charging.common.pojo.vo.ChargingProcessVO;
import cn.tedu.charging.cost.service.CostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: goodman
 * @Date: 2025/3/2 21:35
 */
@Slf4j
@Service
public class CostServiceImpl implements CostService {


    /**
     *
     * 计价逻辑梳理
     *
     * 用户创建订单后 开始充电 直到 充满 不是 一下就充满 可能需要一定的时间
     * 比如 充满需要3个小时 19：00 开始 充满 22:00 充满结束
     * 如果充满需要多个小时 可能 跨越 多个时间段 多个时间段 有可能 要匹配不同的计价规则
     * 19：00 开始 充满 22:00
     * 19:00 -- 20:00  一个计价规则  电费 1.5
     * 20:00 -- 21:00  一个计价规则  电费 1.4
     * 21:00 -- 22:00  一个计价规则  电费 1.3
     *
     * 设备同步充电度数的策略
     *
     * 设备一分钟同步一次充电进度数据
     *
     * 时间    实际充电度数-不包含 已经充的度数-包含        电费   花费
     * 19:00     1                      1               1.5   1.5
     * 19:01     2                      3               1.5    3       丢失了
     * 19:03     2                      5               1.5    3
     * ...
     * 20:01     2                      7               1.4   2.8
     * 20:02     3                      10              1.4   4.2
     * ...
     * 22:01     2                      12              1.3  2.6
     * 22:02     3                      15              1.3  3.9
     *
     * 22:00     充满
     *
     * 总时间  3个小时  22:00 - 19:00
     * 总花费 =  每次充电的花费 累加
     * 总度数 =  每次充电的度数 累加
     *
     * 每分钟同步的充电度数 是 实际的充电度数 ？ 有没有别的同步策略 ？
     * 不包含 每次只同步实际的充电度数 上次同步后，又充电的度数
     * 包含  每次 同步的实际已经充了的度数 包含上次 或者 上上次的度数
     *
     * 我们应该选择哪种同步策略呢？
     *
     * 包含
     * 优点 丢失一两次同步的数据 问题不大 这次同步的数据总是 包含上次的同步的数据
     * 缺点 每次计算价格的时候  计算实际的充电度数 当前同步的度数 - 上次同步的度数
     *
     *
     * 不包含
     * 缺点 如果丢失数据 会导致 少计算了充电度数 导致 价格计算错误，平台造成损失
     * 优点 计算简单 只需拿到 设备同步的实际充电度数 * 每度电的价格 = 本次花费
     *
     *
     * 计算实际充电度数
     * 同步次数  充电度数-包含
     *  1        2
     *  2        5   实际是 5-2 本次充电度数 - 上次充电度数
     *
     *
     * 总花费   每次充电的花费 累加
     * 同步次数  花费
     * 1        5元
     * 2        3元
     * 总花费 5 + 3  = 8
     *
     * 面临了一个问题？
     * 我们需要保存 每次同步的充电进度数据
     * 计算实际充电度数的时候 需要 用本次度数减去上次度数
     * 计算总花费的时候 需要 用本次的花费 加上 上次的花费
     *
     * 我们应该把数据存储到什么地方呢？
     * 能否存储到 本地的一个数据结构 Map中 可以么 ？
     *
     * Map<String,Object> map = new HashMap<>();
     *
     * 思考 map的key 和value 如何设计  map保存的是充电数据 只保存一个订单的么？
     * 计价服务的计算价格接口 可以同时被多个用户调用 不同的用户是不同的订单号
     * map 应该存储所有用户的同步数据
     *
     * 可以同时被多个用户调用 不能是一个订单 一个map吧 ？
     * 可以同时被多个用户调用 存储到 一个map里 不同的订单号有不同的充电进度数据 各同步各的，相互不影响
     * Key   订单号
     * value 充电数据
     *
     *
     * @param chargingProcessParam
     * @return
     */
    @Override
    public ChargingProcessVO calculateCost(ChargingProcessParam chargingProcessParam) {

        log.debug("计算花费入参:{}",chargingProcessParam);
        //当前设备同步的充电度数
        Float chargingCapacity = chargingProcessParam.getChargingCapacity();
        //每度电的价格 * 度数
        //计价规则 价格定义的规则 电费是阶梯的 尖 峰 平 谷 不同的时间段 每度电的价格是不同的
        // 通过什么条件查询计价规则
        // 1 通过站id 查询当前站的所有的计价规则，然后通过程序去匹配一个具体的计价规则 ，通过缓存把计价规则缓存到Redis 减少数据库的查询次数
        // 2 通过sql 不是通过程序 直接从表中查出 一条具体的计价规则 此方案废弃
        //站点id
        Integer stationId = chargingProcessParam.getStationId();
        //枪类型
        Integer gunType = chargingProcessParam.getGunType();
        //获取当前小时
        Integer currentHour = getCurrentHour();
        //匹配一个具体的计价规则
        log.debug("通过站点id:{},枪类型:{},当前时间:{}匹配计价规则",stationId,gunType,currentHour);
        CostRulePO matchCostRule = getMatchCostRule(stationId,gunType,currentHour);
        log.debug("通过站点id:{},枪类型:{},当前时间:{}匹配计价规则:{}",stationId,gunType,currentHour,matchCostRule);

        if (matchCostRule != null) {
            //获取当前匹配到的计价规则的电费
            BigDecimal powerFee = matchCostRule.getPowerFee();
            log.debug("计算价格，设备同步的数据:{},电费:{}",chargingProcessParam,powerFee);
            //BigDecimal cost = calculateCost(chargingCapacity, powerFee);
            //log.debug("计算价格，当前设备同步的充电度数:{},电费:{},计算花费:{}",chargingCapacity,powerFee,cost);
            ChargingData chargingData = getChargingData(chargingProcessParam, powerFee);
            log.debug("计算价格，设备同步的数据:{},电费:{},计算后的充电数据:{}",chargingProcessParam,powerFee,chargingData);
            return buildChargingProcessVO(chargingProcessParam.getOrderNo(),chargingData);
        }else {
            log.error("通过站点id:{},枪类型:{},当前时间:{} 没有匹配计价规则！！！系统的告警，没有计价规则，无法计算用户的花费",stationId,gunType,currentHour);
        }
        return null;
    }

    /**
     * 通过充电数据 构建 计价价格的 出参
     * @return
     */
    private ChargingProcessVO buildChargingProcessVO(String orderNo,ChargingData chargingData){
        ChargingProcessVO chargingProcessVO = new ChargingProcessVO();
        //订单号
        chargingProcessVO.setOrderNo(orderNo);
        //总花费
        chargingProcessVO.setTotalCost(chargingData.getTotalCost());
        //充电度数
        chargingProcessVO.setChargingCapacity(chargingData.getChargingCapacity());
        //开始充电时间
        Long startTime = chargingData.getStartTime();
        //当前时间
        long now = System.currentTimeMillis();
        //当前时间 减去 开始充电的时间（设备第一次同步数据的数据）
        long costTime = now - startTime;
        chargingProcessVO.setChargingTime(costTime);
        return chargingProcessVO;
    }

    /**
     * 获取充电数据，并计算
     * @param chargingProcessParam 计算价格的入参
     * @param powerFee 计价规则 每度电的价格
     * @return
     */
    private ChargingData getChargingData(ChargingProcessParam chargingProcessParam, BigDecimal powerFee) {
        //ChargingData chargingData = getChargingDataByOrderNoFromLocalMap(chargingProcessParam.getOrderNo());
        //把过去同步本地map存取充电数据的方法替换为 通过Redis缓存取存取充电数据
        ChargingData chargingData = getChargingDataByOrderNoFromCache(chargingProcessParam.getOrderNo());
        if (chargingData != null) {
            //获取实际充电度数 当前充电度数 - 上次充电度数
            Float realChargingCapacity = chargingData.getChargingCapacity(chargingProcessParam.getChargingCapacity());
            //当前花费  实际充电度数 *  每度电的价格
            BigDecimal currentCost = calculateCost(realChargingCapacity, powerFee);
            //保存当前设备同步的充电度数到 充电数据chargingData ，为了下一次计算 <<实际充电度数>> 做准备
            chargingData.setChargingCapacity(chargingProcessParam.getChargingCapacity());
            //保存 本次花费 到 充电数据chargingData 为下次计算 <<总花费>> 做准备
            chargingData.setTotalCost(currentCost);
            //保存充电数据到map
            //localChargingDataMap.put(chargingProcessParam.getOrderNo(),chargingData);
            //把过去  保存充电数据到map 替换为 保存到Redis
            chargingDataCacheRepository.saveChargingData(chargingProcessParam.getOrderNo(),chargingData);
        }
        return chargingData;
    }

    /**
     * 通过订单号从 Redis 获取充电数据
     * 如果是第一次 初始化充电数据 并保存到 Redis
     * 如果不是第一次 直接返回
     * @param orderNo
     * @return
     */
    private ChargingData getChargingDataByOrderNoFromCache(String orderNo) {
        //通过订单号从Redis 获取充电数据
        //ChargingData chargingData = localChargingDataMap.get(orderNo);
        ChargingData chargingData = chargingDataCacheRepository.getChargingDataByOrderNo(orderNo);
        //判断是否有充电数据
        if (chargingData == null) {
            //没有充电数据 说明是第一次同步
            //初始化充电数据
            chargingData = new ChargingData();
            //保存充电开始时间
            chargingData.setStartTime(System.currentTimeMillis());
            //度数和花费不需要设置 因为我们在 chargingData 给度数和花费有默认之后 都是 0
            //chargingData.setChargingCapacity(chargingProcessParam.getChargingCapacity());
            //localChargingDataMap.put(orderNo,chargingData);
            //保存初始化的充电数据到 Redis
            chargingDataCacheRepository.saveChargingData(orderNo,chargingData);
            return chargingData;
        }else {
            //直接返回
            return chargingData;
        }
    }

    /**
     * 通过订单号从本地map获取充电数据
     * 如果是第一次 初始化充电数据 并保存到map
     * 如果不是第一次 直接返回
     * @param orderNo
     * @return
     */
    private ChargingData getChargingDataByOrderNoFromLocalMap(String orderNo) {
        //通过订单号从本地map获取充电数据
        ChargingData chargingData = localChargingDataMap.get(orderNo);
        //判断是否有充电数据
        if (chargingData == null) {
            //没有充电数据 说明是第一次同步
            //初始化充电数据
            chargingData = new ChargingData();
            //保存充电开始时间
            chargingData.setStartTime(System.currentTimeMillis());
            //度数和花费不需要设置 因为我们在 chargingData 给度数和花费有默认之后 都是 0
            //chargingData.setChargingCapacity(chargingProcessParam.getChargingCapacity());
            localChargingDataMap.put(orderNo,chargingData);
            return chargingData;
        }else {
            //直接返回
            return chargingData;
        }
    }

    /**
     *  计算价格
     *  每度电的价格 * 度数
     */
    private BigDecimal calculateCost(Float chargingCapacity, BigDecimal powerFee) {
        // multiply 乘法
        return powerFee.multiply(new BigDecimal(chargingCapacity));
    }

    /**
     * 获取当前时间小时
     * @return
     */
    private Integer getCurrentHour(){
        //Date java的时间api 不推荐
        int hour = LocalDateTime.now().getHour();
        return hour;
    }

    /**
     * 先通过本地的map 存储每个订单同步的充电进度数据
     *   Key   订单号
     *   value 充电数据
     *
     *  独享的map
     *  1 重启数据丢失问题
     *  重启后数据就丢失了
     *  原来存储的价格和实际充电度数丢失
     *  导致价格计算错误
     *
     *  同步次数  充电度数 实际充电度数   电费  花费
     *    1        5        5         1    5            如果重启了 数据就丢失了
     *    2        6        1         2    2 + 5 = 7
     *    计算错误的场景 如果跨越了不同的时间段 不同的计价规则 会导致价格计算错误
     *    正确的 花费是 上次的 5 + 本次  2 = 7
     *    错误 实际充电度数 6  * 当前的电费  2 = 总花费  12    多收了 12-5 = 7
     *
     *
     * 2 数据重复计算混乱
     *    计价服务
     *    6060
     *    6061
     *    6062
     *
     *    订单服务 负载均衡的调用 不同的 计价服务 6060 6061 6062
     *    计算价格
     *    第一次 6060  本地map 存了数据  同一个订单  都初始化 都同步数据  导致 计算实际度数 和 总价错误
     *    第二次 6061  本地map 存了数据  同一个订单  都初始化 都同步数据  导致 计算实际度数 和 总价错误
     *    第三次 6062  本地map 存了数据  同一个订单  都初始化 都同步数据  导致 计算实际度数 和 总价错误
     *    第四次 6061  本地map 存了数据  同一个订单  都初始化 都同步数据  导致 计算实际度数 和 总价错误
     *
     *   3 数据量过大问题
     *   每天都有很多的用户在充电，意味着有很多订单，一个订单充满需要同步多次的数据
     *   订单数 * 同步数 = 很多的数据
     *   假定 1分钟同步一次 每个订单平均充满需要 2个小时
     *   1分钟
     *   一个小时 60
     *   2个小时 120
     *   1个订单 2个小时 产生120条数据
     *   1000 订单 * 120条数据 = 120w条数据
     *
     *   本地map 会占用 计价服务的内存 jvm 内存 资源浪费 可能导致 内存溢出 GC 垃圾回收 造成服务卡顿 服务变慢
     *
     *  独享map 每台计价服务 都有自己的 充电数据 他们之间是 不能相互访问
     *
     *  共享 Redis 来存储 充电数据 解决上面3个问题
     *  1 重启数据丢失问题
     *  2 数据独享 计算混乱问题
     *  3 数据量过大问题
     *
     *  mysql 可以解决 但是不推荐 数据量过大的问题 可能需要mysql 分库分表 复杂度提升
     *  mysql 性能相对 Redis 较差
     *
     *  ES 也可用 todo
     *
     *
     */
    private Map<String, ChargingData> localChargingDataMap = new HashMap<>();


    @Autowired
    private ChargingDataCacheRepository chargingDataCacheRepository;


    /**
     * 获取匹配的计价规则
     * 还需要什么样的入参 才能匹配一个具体的计价规则？
     * 每个计价规则 都有开始时间 和 结束时间
     *
     *
     * 1 查询计价规则列表
     * 2 匹配规则 用来计算价格
     * 目前 查询计价规则 是从数据库查询
     * 有优化空间
     *
     * 同步充电数据的时候 会产生大量的数据
     *      每天都有很多的用户在充电，意味着有很多订单，一个订单充满需要同步多次的数据
     *      *   订单数 * 同步数 = 很多的数据
     *      *   假定 1分钟同步一次 每个订单平均充满需要 2个小时
     *      *   1分钟
     *      *   一个小时 60
     *      *   2个小时 120
     *      *   1个订单 2个小时 产生120条数据
     *      *   1000 订单 * 120条数据 = 120w条数据
     *
     * 同步充电数据的时候 会产生大量的mysql查询 每次同步充电数据都需要查询计价规则用来计算花费
     *      *   订单数 * 同步数 = 很多的数据
     *      *   假定 1分钟同步一次 每个订单平均充满需要 2个小时
     *      *   1分钟
     *      *   一个小时 60
     *      *   2个小时 120
     *      *   1个订单 2个小时 查询 120次mysql
     *      *   1000 订单 * 120条数据 = 查询120W次 mysql
     *
     *  mysql的性能 相对 Redis差很多
     *  优化 把计价规则 缓存到Redis 来提升查询规则的性能，计算价格的接口 响应时间（RT）也会提升
     *
     * @param stationId
     * @param gunType 枪类型
     * @param hour 当前时间 小时
     * @return
     */
    private CostRulePO getMatchCostRule(Integer stationId, Integer gunType, Integer hour){
        log.debug("通过站id:{}查询计价规则列表",stationId);
        //在查询计价规则之前记录开始时间
        long start = System.currentTimeMillis();
        List<CostRulePO> costRulePOS = getCostRuleListByStationId(stationId);
        //在查询计价规则之后记录结束时间
        long end = System.currentTimeMillis();
        //用结束时间 减去 开始时间 得到 花费的时间
        long cost = end - start;
        log.debug("通过站id:{}查询计价规则列表:{},花费时间:{}",stationId,costRulePOS,cost);
        if (!CollectionUtils.isEmpty(costRulePOS)) {
            //进行计价规则匹配
            //1 枪类型 && 2 当前时间 在匹配时间段内
            for (CostRulePO costRulePO : costRulePOS) {
                //计价规则的开始时间
                Integer startTime = costRulePO.getStartTime();
                //计价规则的结束时间
                Integer endTime = costRulePO.getEndTime();
                //枪类型
                Integer costRuleGunType = costRulePO.getGunType();
                //枪类型 匹配
                if (gunType.equals(costRuleGunType)
                        //时间范围 匹配
                        // 当前时间 21  20 --> 22 一个计价规则
                        // 21 > 20 && 21 < 22 条件匹配
                        // 当前 22 20-22 一个计价规则 22-24 一个计价   只能匹配一个
                        && hour >= startTime && hour < endTime) {
                    return costRulePO;
                }
            }
        }else {
            log.debug("通过站id:{}查询计价规则列表为空",stationId);
        }
        return null;
    }


    @Autowired
    private CostRuleRepository costRuleRepository;

    @Autowired
    private CostRuleCacheRepository costRuleCacheRepository;

    /**
     * 通过站id查询 计价规则列表
     *
     *
     *
     * 1> 目前方案 不提前准备 用的时候再查  导致第一次同步的时候 慢
     * 2> 提前准备的方案 用的时候直接用  导致项目启动的时候 慢
     *  可以参考 设备服务的 缓存预热逻辑 StationDataInit 把计价规则在项目启动时缓存到Redis
     */
    private List<CostRulePO> getCostRuleListByStationId(Integer stationId){
        //1 第一次先从 Redis 查
        List<CostRulePO> cacheCostRules = costRuleCacheRepository.getCostRuleListByStationId(stationId);
        //2 判断Redis是否有数据
        if (CollectionUtils.isEmpty(cacheCostRules)) {
            //第一次 Redis 没有
            //3 从数据库查询
            List<CostRulePO> dbCostRules = costRuleRepository.getCostRuleListByStationId(stationId);
            //4 判断数据库是否有数据
            if (!CollectionUtils.isEmpty(dbCostRules)) {
                //4 数据库有数据 把查询到的数据  存到 Redis
                costRuleCacheRepository.saveCostRules(stationId,dbCostRules);
                //5 返回数据
                return dbCostRules;
            }else {
                //数据库没有数据
                log.error("告警告警告警，业务停止 停止充电，未查询到计价规则");
            }
        }else {
            //Redis有数据 返回数据
            return cacheCostRules;
        }
        return null;
        //return costRuleRepository.getCostRuleListByStationId(stationId);
    }

}

package cn.tedu.charging.order.dao.repository;


import cn.tedu.charging.order.pojo.po.ChargingProcessPO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 操作ES的Repository
 * extends ElasticsearchRepository 实现crud
 * @Author: goodman
 * @Date: 2025/3/9 21:10
 */
public interface ChargingProcessESRepository extends ElasticsearchRepository<ChargingProcessPO, Long> {
}

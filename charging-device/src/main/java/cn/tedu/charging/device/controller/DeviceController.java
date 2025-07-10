package cn.tedu.charging.device.controller;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.param.UpdateGunStatusParam;
import cn.tedu.charging.device.pojo.vo.StationInfoVO;
import cn.tedu.charging.device.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequestMapping("device")
@RestController
@Api(tags = "设备服务")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;


    @ApiOperation("充电站详情")
    @GetMapping("/station/info/{stationId}")
    public JsonResult<StationInfoVO> getStationInfo(@PathVariable("stationId") Integer stationId) {
        log.debug("充电站详情入参:id:{}",stationId);
        long start = System.currentTimeMillis();
        StationInfoVO stationInfoVO = deviceService.getStationInfoByStationId(stationId);
        long end = System.currentTimeMillis();
        log.debug("通过id查询场站信息消耗时间:{}",end-start);
        JsonResult result = JsonResult.ok(stationInfoVO);
        log.debug("充电站详情入参:id:{},出参:{}",stationId,result);
        return result;
    }


    @ApiOperation("修改枪的状态")
    @PostMapping("/station/gun/status/update")
    public JsonResult<Boolean> updateGunStatus(@RequestBody UpdateGunStatusParam param) {
        log.debug("修改枪的状态入参:id:{}",param);
        Boolean updateResult = deviceService.updateGunStatus(param);
        JsonResult result = JsonResult.ok(updateResult);
        log.debug("修改枪的状态入参:id:{},出参:{}",param,result);
        return result;
    }



    @ApiOperation("附近的充电站")
    @GetMapping("/station/near")
    public JsonResult<List<StationInfoVO>> nearStation(Double longitude, Double latitude, Double radius) {
        log.debug("附近充电站接口入参:longitude:{},latitude:{},radius:{}",longitude,latitude,radius);
        List<StationInfoVO> stationInfoVOs = deviceService.nearStation(longitude,latitude,radius);
        JsonResult result = JsonResult.ok(stationInfoVOs);
        log.debug("附近充电站接口入参:longitude:{},latitude:{},radius:{},出参:{}",longitude,latitude,radius,result);
        return result;
    }

}

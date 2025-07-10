package cn.tedu.charging.device.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * 和数据库表  charging_station 做映射
 * ORM 对象关系映射
 * StationPO charging_station 映射
 */
@Data
@TableName("charging_station")
public class StationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("station_code")
    private String stationCode;

    @TableField("station_name")
    private String stationName;

    @TableField("device_number")
    private Integer deviceNumber;

    @TableField("ac_gun_number")
    private Integer acGunNumber;

    @TableField("dc_gun_number")
    private Integer dcGunNumber;

    @TableField("ac_rate_power")
    private Integer acRatePower;

    @TableField("dc_rate_power")
    private Integer dcRatePower;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("address")
    private String address;

    @TableField("station_lng")
    private BigDecimal stationLng;

    @TableField("station_lat")
    private BigDecimal stationLat;

    @TableField("device_power")
    private Integer devicePower;

    @TableField("station_model")
    private Integer stationModel;

    @TableField("station_status")
    private Integer stationStatus;

    @TableField("station_type")
    private Integer stationType;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("operator_id")
    private Integer operatorId;

    @TableField("park_fee")
    private BigDecimal parkFee;

    @TableField("deleted")
    private Integer deleted;
}
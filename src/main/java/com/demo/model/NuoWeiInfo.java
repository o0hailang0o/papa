package com.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("nuo_wei_info")
public class NuoWeiInfo {

     @TableId(type=IdType.AUTO)
     private Integer id ;
     private String season;
     private Date matchTime;
     private String rounds;
	 private String SortHost;
	 private String host;
	 private String sortGuest;
	 private String guest;
	 private Integer hostScore;
	 private Integer guestScore;
	 private Integer halfHostScore;
	 private Integer halfGuestScore;
	 private BigDecimal winPrice;
	 private BigDecimal evenPrice;
	 private BigDecimal lostPrice;
	 private BigDecimal concedeWinPrice;
	 private BigDecimal concedeEvenPrice;
	 private BigDecimal concedeLosePrice;
     private Integer concede;
}

package com.ggeit.pay.config;

import java.util.HashMap;

/**
 * 支付宝配置
 * @author Administrator
 *
 */
public class ALIPayConstants {

	// 支付宝网关
	public static final String URL = "https://openapi.alipay.com/gateway.do";
	// 编码
	public static String CHARSET = "UTF-8";
	// 返回格式
	public static String FORMAT = "json";
	
	// RSA2(2048),Java版本
	// 商户应用私钥--国光一码付应用
	public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpmKJDbTUe61/QbN8x2mhuBet3J/1HcMQL2lNr4eEdQI0Q+6xz/vluAX7CEdTw2FeP5AN1+cQwB9Ini7jEtF6fF6bUKMmeD4OA0byfDiqbinbJ0Pe0/qLOxzzeX2iqRzRHUC8KP4029rgE6nyMHdP9Un6/pbIaj+HRkJ+xlYJ5dcASg1aCu5LunKrk63CGcVAm7UA7liXfhCzfG8/RbwiO1tRXkKo2AgWM75j6NiiDfmmpVkIXocSEGupBL15Kxfv2hV7P0iChsQ0NNrbdEzm3nNHVW0VFwnw5HlTz3VeOjn3hDrNNXuG6vytzUj3foSkGRUhG8Cgk+woxfGe4nGt7AgMBAAECggEBAJiQ4ofdmkfyXgNVrYNfg0pWsPhEVSkLXJF9GkaWsLhr9XstrHm9Cg3X8nwLJpBzTnH3INXkloTOxFMm8lSiHezojf+Voao7MoWUKCa9y76Y1TvAbNW5rCRwP2WcVr+3xmPQ0kBZ42Nqlyh0+Q3jV20v5S3qlUon3XF0tPRAF1vDBalEIJWDgJvXulwIpRNuZuPLt+TFMYftSE/UHNJ7jHQ55jKpjW2/VdHfNgYgOHYK+z2EvkQcnCgnNoKGDcDhW4cTk/loVOCwvUY/5sCotX+R5nWw6xzg3vXOMbdQuc/mjTkTg14CBKoLvo4TXdJUlPqKpek89tAoc8J0k6L+IcECgYEA4cWgB2Ikj5VEKGmogfnSsvzm+8P2KxvN47fr7Deuhw/x+k5di3RPAwnGFZ0eTz7GHkvT/EOhEfpmWWAhIpmT1dSbbFUQnZVFMbnjjUgbDR1zp9QP15QQfMipLwBmOxDqDJrsU5YrFGrie5a3zNJ9pFdVAlnvQwWfX1L+ig+N+MMCgYEAwE2TLaruxNLouzoHOuuCEReIXJugOucmVNZnDMDqzb7+BqvpmOXOTFedJ9dXzpyKFmw4a9PZ7cwXiiZa4YALuipkzSq4AE74y/VapOGV/vSe3TyCA/HNzlxOADghmXCYxAVczQdfARIqfziQLVKalcLCEegO6VaYJXIx+Nym1ukCgYARgduo9ulS4QZKA/d3LMIz8vyOZWG/cgaDNA3tYVv9STVAbbc/SyOdXrKnlml1R67hRowHsc7ZsAsjvxqSrkoSeMwcmHQTZDHba0jD+eVvHOuNBhq8YtTseDr2VBeZWPRtSc4tpSVuDePGJafXEDOB/OuwpPyyu8rh/3ORjSc7NQKBgDO7KqKYXw73pb/oBlXlUOIEMJypCNuPnxIncyVNKF0cyf1Fddu5xltnHlG/blGYbtVFW5A2N2PvCU4Sr1QGPCFikidXHJSIqglEAZRlUxJ4/9uIEXm6LKEc16bbcr5yOcZRovtMxlPlbSU4NCDdHdS3xblHcet3bgp0w9iskv0xAoGAMpo7V82v0sIpgBQLIeGmkIj44TsJnizrQU+3i7OBf78E4cVYNeCvzVu98nBd5w9AlzfVC2LjCQYdrhI+O0UoC6UMDGp6T8tPbRU/r/FzkmRMeKNkEIZlGao4QeYYntELrmmb2YQCsMh+lN2CMqKMUw9s7dPCTKYJFbOEdKrnUtw=";
	// 支付宝公钥
	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgw/REIIdVtIIL9tAsCLRQcrVRCE3OIuu/O6aGrALwZm2RhThQL/lbf4pQTWu9bBA6aYD+ykfuK/cGa2bzZaQr8A5GtaL6s5E2fMYSp6RRJg+eiQXWcnI+sh3CxnvXvf90rJuoPRs4sLgHDASg7rMjTGtfXYc7N0zWeAGvt5mJBKK8fDCuyexN5wMv0Iujj4zD/u8hz5mOdRKo6H+rhcTZSfr+KLl1EpuW5eJzYj4RyQR01tjA1+CJZKeT1Xqa7RAEouKmVYG92rK2DFDQvYBdWfLQ2gtdWJg7IuJ+Wz0LC2ClvMXK9k7C5mbuF40tOyKlA8ShkNThsFF7aGCp2agHwIDAQAB";
	// RSA2
	public static String SIGNTYPE = "RSA2";
	
	public static HashMap<String, String> remarkmap = new HashMap<String, String>();
	
	static {
		remarkmap.put("1001", "门诊挂号");
		remarkmap.put("1002", "门诊缴费");
	}
}

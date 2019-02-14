package com.baicai.corewith.util;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发号器
 * 暂未考虑严格的集群实现和持久化需求
 * 集群环境需要考虑服务化的网络延迟远大于生成器的生成时间，因而可采用大池+小池的思路
 * @Description: TODO
 * @author 猪肉有毒 waitfox@qq.com
 * @date 2016年6月19日 下午8:00:04
 * @version V1.0 我只为你回眸一笑，即使不够倾国倾城，我只为你付出此生，换来生再次相守
 */
public class IDFactory{
	private static final AtomicInteger id = new AtomicInteger();
	private int sourceId=1;//来源，未不同的机器/app分配不同的sourceid
	private static final int limit=999999;//序列边界，每秒最多产生的序列号个数

	private static class IDFactoryHolder {
		private static final IDFactory INSTANCE = new IDFactory();
	}

	private IDFactory() {
		id.set(1);	
	}

	public static final IDFactory getInstance() {
		return IDFactoryHolder.INSTANCE;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public  long fetch() {
		long timestamp = System.currentTimeMillis() / 1000;
		int atmoic = id.getAndIncrement();
		if(atmoic>=limit){
			id.set(1);
			atmoic=1;
		}
		StringBuilder sb=new StringBuilder(17);
		sb.append(sourceId);
		sb.append(timestamp).append(atmoic);
		return Long.parseLong(sb.toString(),10);
	}
	
	/**
	 * 更快的发号器，使用数字运算取代了字符串拼接和转换过程中的性能消耗
	 * 每秒最多可产生近一亿个数字，比拼接字符串的做法快了20倍。
	 * 但是，程序限制了每秒最多100W-1个，足以。
	 * @return
	 */
	public  long quickFetch() {
		long timestamp = System.currentTimeMillis() / 1000;
		int atmoic = id.getAndIncrement();
		if(atmoic>=limit){
			id.set(1);
			atmoic=1;
		}
		long result=sourceId*10000000000000000l;
		result+=timestamp*1000000l;
		result+=atmoic;
		return result;
	}
	
}

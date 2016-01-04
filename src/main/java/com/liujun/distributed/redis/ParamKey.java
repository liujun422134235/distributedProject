package com.liujun.distributed.redis;

public enum ParamKey
{
	/**
	 * 锁加的名称
	 */
	LOCK_KEY_NAME("lock.user"),
	
	/**
	 * 加锁的最大时长,单位为秒
	 */
	LOCK_MAX_WAIT("4"),
	;
	
	private String key;

	private ParamKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
	
}

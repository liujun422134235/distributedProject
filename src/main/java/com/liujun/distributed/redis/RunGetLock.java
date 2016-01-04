package com.liujun.distributed.redis;

import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RunGetLock implements Runnable
{
	/**
	 * 非切片额客户端连接
	 */
	private Jedis jedis;

	/**
	 * 设置一个默认业务运行时长
	 */
	private AtomicInteger runTime = new AtomicInteger(4000);

	public void setRunTime(Integer runTime)
	{
		this.runTime.set(runTime);
	}

	public RunGetLock()
	{
		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(10);
		config.setMaxIdle(3);
		config.setMaxWait(1000l);
		config.setTestOnBorrow(true);
		JedisPool jedisPool = new JedisPool(config, "vm1", 6379);
		jedis = jedisPool.getResource();
	}

	@Override
	public void run()
	{
		while (true)
		{
			// 检查当前是否存在此key的信息
			long lockRsp = jedis.setnx(ParamKey.LOCK_KEY_NAME.getKey().getBytes(),
					String.valueOf(System.currentTimeMillis()).getBytes());

			// 检查当前是否加锁成功
			if (1 == lockRsp)
			{
				// 则进行业务流程操作
				runService();

				// 业务完成之后，释放锁
				jedis.del(ParamKey.LOCK_KEY_NAME.getKey().getBytes());

				// 加锁成功之后将当前线程暂停3秒
				try
				{
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			// 说明当前已经被其他线程加锁
			else
			{
				byte[] getValue = jedis.get(ParamKey.LOCK_KEY_NAME.getKey().getBytes());
				if (null != getValue)
				{
					// 取得前一成业务加锁成功的时间，检查是否已经超过30秒
					String lockSucTimeStr = new String(getValue);

					// 与当前时间作对比
					Long lockSucTime = Long.parseLong(lockSucTimeStr);
					Long currTime = System.currentTimeMillis();

					// 如果当前已经超时
					if (currTime - Long.parseLong(ParamKey.LOCK_MAX_WAIT.getKey())*1000 > lockSucTime)
					{
						byte[] updTimebys = jedis.getSet(ParamKey.LOCK_KEY_NAME.getKey().getBytes(),
								String.valueOf(System.currentTimeMillis()).getBytes());

						if (null != updTimebys)
						{
							String updTime = new String(updTimebys);

							// 如果更新后获取的老时间与上次获取的时间相同，说明更新成功
							if (updTime.equals(lockSucTimeStr))
							{
								runService();

								// 业务完成之后，释放锁
								jedis.del(ParamKey.LOCK_KEY_NAME.getKey().getBytes());
							}
						}
					} else
					{
						// 让当前线程休眠，等待，过会再进行重试
						try
						{
							Thread.currentThread().sleep(3000l);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 进行业务方法调用
	 */
	private void runService()
	{
		System.out.println("当前:" + Thread.currentThread().getId() + "执行业务方法");
		try
		{
			Thread.currentThread().sleep(runTime.get());
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("当前:" + Thread.currentThread().getId() + "执行业务方法结束");

	}

}

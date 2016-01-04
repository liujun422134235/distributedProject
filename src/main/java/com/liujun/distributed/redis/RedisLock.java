package com.liujun.distributed.redis;

public class RedisLock
{
	public static void main(String[] args)
	{
		//启动线程，进行加锁
		Thread thr1 = new Thread(new RunGetLock());
		Thread thr2 = new Thread(new RunGetLock());
		Thread thr3 = new Thread(new RunGetLock());
		Thread thr4 = new Thread(new RunGetLock());
		
		
		thr1.start();
		thr2.start();
		thr3.start();
		thr4.start();
	}
}

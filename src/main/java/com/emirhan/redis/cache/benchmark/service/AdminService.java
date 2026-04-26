package com.emirhan.redis.cache.benchmark.service;


public interface AdminService {
    void cleanAllDatabases();



    void factoryReset(Integer productCount);
}

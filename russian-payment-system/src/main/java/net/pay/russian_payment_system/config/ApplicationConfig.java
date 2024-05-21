package net.pay.russian_payment_system.config;

import net.pay.russian_payment_system.cbrrate.CachedCurrencyRates;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@Configuration
@EnableConfigurationProperties(CbrConfig.class)
public class ApplicationConfig {
    private final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

    @Bean
    public Cache<LocalDate, CachedCurrencyRates> currencyRateCache(@Value("${app.cache.size}") int cacheSize) {
        return cacheManager.createCache("Currency Rate Cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(LocalDate.class,
                        CachedCurrencyRates.class, ResourcePoolsBuilder.heap(cacheSize))
                        .build());
    }
}

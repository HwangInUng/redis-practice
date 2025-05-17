package com.redispractice.config

import com.redispractice.domain.entity.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    // Bean을 User 타입으로만 지정하는 경우 범용성이 떨어짐
    // 이 부분을 해결하기 위해서는 Any 타입으로 지정하는 방법이 있음.
    @Bean
    fun redisTemplateForUser(connectionFactory: RedisConnectionFactory): RedisTemplate<String, User> {
        val template = RedisTemplate<String, User>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(User::class.java)

        return template
    }

    // 이렇게 설정하면 모든 타입에 대한 직렬화 지원이 가능
    // 다만 다음과 같은 문제점이 발생할 수 있음
    // 1. Any는 모든 kotlin 객체의 부모 타입이기 때문에, Any로 지정된 객체를 역직렬화할 때 어떤 타입으로 역직렬화할지 알 수 없음
    // 2. activeDefaultTyping을 사용하면 모든 객체에 대해 역직렬화 시 타입 정보를 포함시켜야 하기 때문에 reflection을 동적으로 생성하면서 임의의 악성 클래스가 로드될 가능성
    // 3. Jackson2JsonRedisSerializer는 기본적으로 private/protected 필드에 대한 접근을 허용하지 않기 때문에, private/protected 필드가 있는 객체를 직렬화/역직렬화할 수 없음
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(Any::class.java)

        return template
    }

    // 이런 문제를 원초적으로 해결하기 위해서는 Redis를 구현하는 Repository<T>를 만들어 도메인 타입별 구현체 생성
    // RedisRepositoryConfig 확인
}
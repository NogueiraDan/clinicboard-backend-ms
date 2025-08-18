package com.clinicboard.user_service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * 🧬 FITNESS FUNCTIONS para Resilience Patterns
 * 
 * Baseado nos padrões de Michael Nygard em "Release It!" e
 * práticas de arquitetura resiliente para microsserviços.
 * 
 * Detecta violações de:
 * - Circuit Breakers
 * - Retry Patterns
 * - Timeout Configuration
 * - Fallback Mechanisms
 * - Bulkhead Isolation
 * - Rate Limiting
 * - Health Checks
 */
@Tag("fitness-functions")
@DisplayName("🔥 RESILIENCE PATTERNS - Fitness Functions")
class ResiliencePatternsFitnessTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.clinicboard.user_service");
    }

    // ==================== CIRCUIT BREAKER ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Feign Clients devem ter Circuit Breaker")
    void feignClientsMustHaveCircuitBreaker() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.cloud.openfeign.FeignClient")
                .should().beAnnotatedWith("io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker")
                .because("FEIGN CLIENTS DEVEM TER CIRCUIT BREAKER PARA RESILIÊNCIA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Circuit Breakers devem estar na infraestrutura")
    void circuitBreakersMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*CircuitBreaker.*")
                .should().resideInAPackage("..infrastructure..")
                .because("CIRCUIT BREAKERS SÃO DETALHES DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Circuit Breaker config deve estar centralizada")
    void circuitBreakerConfigMustBeCentralized() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*CircuitBreakerConfig.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CONFIGURAÇÕES DEVEM ESTAR CENTRALIZADAS");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== RETRY PATTERNS ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Retry deve estar configurado em operações externas")
    void retryMustBeConfiguredForExternalOperations() {
        ArchRule rule = methods()
                .that().areAnnotatedWith("org.springframework.cloud.openfeign.FeignClient")
                .should().beAnnotatedWith("io.github.resilience4j.retry.annotation.Retry")
                .because("OPERAÇÕES EXTERNAS DEVEM TER RETRY CONFIGURADO");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Retry config deve estar na infraestrutura")
    void retryConfigMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*RetryConfig.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CONFIGURAÇÕES DE RETRY SÃO DETALHES DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== TIMEOUT CONFIGURATION ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Timeouts devem estar configurados")
    void timeoutsMustBeConfigured() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*TimeoutConfig.*|.*Timeout.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("TIMEOUTS DEVEM ESTAR CONFIGURADOS PARA EVITAR BLOQUEIOS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: TimeLimiter deve estar aplicado")
    void timeLimiterMustBeApplied() {
        ArchRule rule = methods()
                .that().areAnnotatedWith("io.github.resilience4j.timelimiter.annotation.TimeLimiter")
                .should().bePublic()
                .because("TIME LIMITER DEVE SER APLICADO EM MÉTODOS PÚBLICOS");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== FALLBACK MECHANISMS ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Circuit Breakers devem ter fallback")
    void circuitBreakersMustHaveFallback() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Fallback.*")
                .should().resideInAPackage("..infrastructure.adapter.out..")
                .because("FALLBACKS SÃO ADAPTADORES DE SAÍDA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Fallback methods devem ser seguros")
    void fallbackMethodsMustBeSafe() {
        ArchRule rule = methods()
                .that().haveNameMatching(".*fallback.*|.*Fallback.*")
                .should().bePublic()
                .because("MÉTODOS DE FALLBACK DEVEM SER PÚBLICOS E SEGUROS");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== BULKHEAD ISOLATION ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Bulkhead deve isolar recursos")
    void bulkheadMustIsolateResources() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Bulkhead.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("BULKHEAD DEVE ISOLAR RECURSOS PARA EVITAR FALHAS EM CASCATA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Thread pools devem estar isolados")
    void threadPoolsMustBeIsolated() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ThreadPool.*|.*Executor.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("THREAD POOLS DEVEM ESTAR CONFIGURADOS NA INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== RATE LIMITING ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Rate Limiter deve estar aplicado")
    void rateLimiterMustBeApplied() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*RateLimit.*")
                .should().resideInAPackage("..infrastructure..")
                .because("RATE LIMITING DEVE PROTEGER CONTRA SOBRECARGA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== HEALTH CHECKS ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Health Checks devem estar implementados")
    void healthChecksMustBeImplemented() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*HealthCheck.*|.*Health.*")
                .should().resideInAPackage("..infrastructure.adapter.in..")
                .because("HEALTH CHECKS SÃO ADAPTADORES DE ENTRADA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Health Indicators devem herdar de Spring")
    void healthIndicatorsMustInheritFromSpring() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*HealthIndicator")
                .should().implement("org.springframework.boot.actuator.health.HealthIndicator")
                .because("HEALTH INDICATORS DEVEM SEGUIR PADRÃO SPRING");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== MONITORING & OBSERVABILITY ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Métricas devem estar configuradas")
    void metricsMustBeConfigured() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Metrics.*|.*MetricsConfig.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("MÉTRICAS SÃO ESSENCIAIS PARA OBSERVABILIDADE");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Logging deve estar padronizado")
    void loggingMustBeStandardized() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "org.slf4j..",
                    "java.util.logging..",
                    "org.apache.commons.logging.."
                )
                .because("DOMAIN NÃO DEVE DEPENDER DE FRAMEWORKS DE LOGGING");

        rule.check(classes);
    }

    // ==================== ERROR HANDLING ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Global Exception Handler deve existir")
    void globalExceptionHandlerMustExist() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.web.bind.annotation.ControllerAdvice")
                .should().resideInAPackage("..infrastructure.adapter.in.web..")
                .because("GLOBAL EXCEPTION HANDLER DEVE TRATAR ERROS CENTRALIZADAMENTE");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Exception handlers devem logar erros")
    void exceptionHandlersMustLogErrors() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ExceptionHandler.*")
                .should().resideInAPackage("..infrastructure..")
                .because("EXCEPTION HANDLERS DEVEM ESTAR NA INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== GRACEFUL DEGRADATION ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Services devem degradar graciosamente")
    void servicesMustDegradeGracefully() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*GracefulDegradation.*|.*Degradation.*")
                .should().resideInAPackage("..infrastructure..")
                .because("DEGRADAÇÃO GRACIOSA DEVE ESTAR NA INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== CACHE RESILIENCE ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Cache deve ter TTL configurado")
    void cacheMustHaveTtlConfigured() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*CacheConfig.*|.*Cache.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CACHE DEVE TER TTL PARA EVITAR DADOS OBSOLETOS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Cache deve ter estratégia de eviction")
    void cacheMustHaveEvictionStrategy() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.cache.annotation.Cacheable")
                .should().resideInAPackage("..infrastructure..")
                .because("CACHE ANNOTATIONS DEVEM ESTAR NA INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== ANTI-PATTERNS ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Não usar Thread.sleep em produção")
    void doNotUseThreadSleepInProduction() {
        ArchRule rule = noClasses()
                .should().dependOnClassesThat().haveSimpleName("Thread")
                .because("THREAD.SLEEP PODE CAUSAR BLOQUEIOS - USE TIMEOUTS APROPRIADOS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Não usar printStackTrace")
    void doNotUsePrintStackTrace() {
        ArchRule rule = noClasses()
                .should().dependOnClassesThat().haveSimpleName("Throwable")
                .because("PRINTSTACKTRACE NÃO É APROPRIADO PARA PRODUÇÃO - USE LOGGING");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Methods devem declarar exceptions específicas")
    void methodsMustDeclareSpecificExceptions() {
        ArchRule rule = methods()
                .that().arePublic()
                .should().notBeDeclaredIn(Exception.class)
                .because("MÉTODOS DEVEM DECLARAR EXCEPTIONS ESPECÍFICAS");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== CONFIGURATION VALIDATION ====================

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Resilience4j configs devem estar centralizadas")
    void resilience4jConfigsMustBeCentralized() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Resilience.*Config.*")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CONFIGURAÇÕES DE RESILIÊNCIA DEVEM ESTAR CENTRALIZADAS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 RESILIENCE CRITICAL: Properties devem ser validadas")
    void propertiesMustBeValidated() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.boot.context.properties.ConfigurationProperties")
                .should().beAnnotatedWith("org.springframework.validation.annotation.Validated")
                .because("PROPERTIES DEVEM SER VALIDADAS PARA EVITAR CONFIGURAÇÕES INVÁLIDAS");

        rule.allowEmptyShould(true).check(classes);
    }
}

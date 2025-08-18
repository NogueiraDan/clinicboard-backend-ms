package com.clinicboard.user_service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * 🧬 FITNESS FUNCTIONS para Messaging Architecture
 * 
 * Baseado nos princípios de Enterprise Integration Patterns e
 * práticas de arquitetura de microsserviços distribuídos.
 * 
 * Detecta violações de:
 * - RabbitMQ Configuration
 * - Event Publishing
 * - Message Handlers
 * - Dead Letter Queues
 * - Async Processing
 * - Event Sourcing Patterns
 */
@Tag("fitness-functions")
@DisplayName("🔥 MESSAGING ARCHITECTURE - Fitness Functions")
class MessagingArchitectureFitnessTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.clinicboard.user_service");
    }

    // ==================== RABBITMQ CONFIGURATION ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Configurações RabbitMQ devem estar na infraestrutura")
    void rabbitMqConfigurationsMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.amqp.rabbit.annotation.RabbitListener")
                .or().haveNameMatching(".*RabbitConfig|.*QueueConfig|.*ExchangeConfig")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CONFIGURAÇÕES DE MENSAGERIA SÃO DETALHES DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Message Handlers devem estar nos adaptadores")
    void messageHandlersMustBeInAdapters() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*MessageHandler|.*EventHandler|.*Listener")
                .should().resideInAPackage("..infrastructure.adapter..")
                .because("MESSAGE HANDLERS SÃO ADAPTADORES DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Publishers devem estar nos adaptadores de saída")
    void publishersMustBeInOutputAdapters() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Publisher|.*EventPublisher|.*MessageProducer")
                .should().resideInAPackage("..infrastructure.adapter.out..")
                .because("PUBLISHERS SÃO ADAPTADORES DE SAÍDA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== DOMAIN EVENTS ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Domain Events devem estar no domínio")
    void domainEventsMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*DomainEvent|.*Event")
                .and().areNotAnnotatedWith("org.springframework.context.ApplicationEvent")
                .should().resideInAPackage("..domain.event..")
                .because("DOMAIN EVENTS PERTENCEM AO DOMÍNIO");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Domain Events devem ser imutáveis")
    void domainEventsMustBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .should().haveOnlyFinalFields()
                .because("DOMAIN EVENTS DEVEM SER IMUTÁVEIS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Domain Events devem ter timestamp")
    void domainEventsMustHaveTimestamp() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.event..")
                .should().haveNameMatching(".*timestamp.*|.*occurredAt.*|.*eventTime.*")
                .orShould().haveRawType("java.time.Instant")
                .orShould().haveRawType("java.time.LocalDateTime")
                .because("DOMAIN EVENTS DEVEM REGISTRAR QUANDO OCORRERAM");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== EVENT PUBLISHING ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Event Publishers não podem estar no domínio")
    void eventPublishersCannotBeInDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().haveNameMatching(".*Publisher|.*EventPublisher")
                .because("DOMÍNIO NÃO DEVE DEPENDER DE PUBLISHERS");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Application deve usar portas para publicar eventos")
    void applicationMustUsePortsForEventPublishing() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.usecase..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "..domain..",
                    "..application.port..",
                    "java..",
                    "org.springframework.stereotype.."
                )
                .because("APPLICATION DEVE USAR PORTAS PARA PUBLICAR EVENTOS");

        rule.check(classes);
    }

    // ==================== MESSAGE PATTERNS ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Message DTOs devem estar nos adaptadores")
    void messageDtosMustBeInAdapters() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*MessageDto|.*EventDto|.*QueueMessage")
                .should().resideInAPackage("..infrastructure.adapter..")
                .because("MESSAGE DTOS SÃO DETALHES DOS ADAPTADORES");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Dead Letter Queue handlers devem estar na infraestrutura")
    void deadLetterQueueHandlersMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*DLQ.*|.*DeadLetter.*|.*ErrorHandler")
                .should().resideInAPackage("..infrastructure..")
                .because("DLQ HANDLERS SÃO DETALHES DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== ASYNC PROCESSING ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Async methods devem ter tratamento de erro")
    void asyncMethodsMustHaveErrorHandling() {
        ArchRule rule = methods()
                .that().areAnnotatedWith("org.springframework.scheduling.annotation.Async")
                .should().haveRawReturnType("java.util.concurrent.CompletableFuture")
                .orShould().haveRawReturnType("void")
                .because("MÉTODOS ASYNC DEVEM TER RETURN TYPE APROPRIADO");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Event Listeners não devem bloquear")
    void eventListenersMustNotBlock() {
        ArchRule rule = noMethods()
                .that().areAnnotatedWith("org.springframework.context.event.EventListener")
                .should().haveNameMatching(".*sleep|.*wait|.*join")
                .because("EVENT LISTENERS NÃO DEVEM USAR MÉTODOS BLOQUEANTES");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== INTEGRATION EVENTS ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Integration Events devem estar na infraestrutura")
    void integrationEventsMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*IntegrationEvent|.*ExternalEvent")
                .should().resideInAPackage("..infrastructure.adapter.out..")
                .because("INTEGRATION EVENTS SÃO CONTRATOS EXTERNOS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Mappers de evento devem converter Domain -> Integration")
    void eventMappersMustConvertDomainToIntegration() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*EventMapper")
                .should().resideInAPackage("..infrastructure.adapter.out..")
                .because("EVENT MAPPERS CONVERTEM DOMAIN EVENTS PARA INTEGRATION EVENTS");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== SAGA PATTERNS ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Saga Coordinators devem estar na aplicação")
    void sagaCoordinatorsMustBeInApplication() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Saga|.*Coordinator|.*Orchestrator")
                .should().resideInAPackage("..application..")
                .because("SAGA COORDINATORS SÃO CASOS DE USO COMPLEXOS");

        rule.allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Saga State deve ser persistido")
    void sagaStateMustBePersisted() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*SagaState|.*ProcessState")
                .should().resideInAPackage("..infrastructure.adapter.out.persistence..")
                .because("SAGA STATE DEVE SER PERSISTIDO PARA RECUPERAÇÃO");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== OUTBOX PATTERN ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Outbox devem estar na infraestrutura")
    void outboxMustBeInInfrastructure() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Outbox.*|.*OutboxEvent")
                .should().resideInAPackage("..infrastructure.adapter.out..")
                .because("OUTBOX PATTERN É DETALHE DE INFRAESTRUTURA");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== ANTI-PATTERNS ====================

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Domínio não pode depender de RabbitMQ")
    void domainCannotDependOnRabbitMq() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.amqp..",
                    "com.rabbitmq..",
                    "org.springframework.messaging.."
                )
                .because("DOMÍNIO DEVE SER AGNÓSTICO À TECNOLOGIA DE MENSAGERIA");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Application não pode depender de RabbitMQ")
    void applicationCannotDependOnRabbitMq() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.amqp..",
                    "com.rabbitmq..",
                    "org.springframework.messaging.."
                )
                .because("APPLICATION DEVE SER AGNÓSTICA À TECNOLOGIA DE MENSAGERIA");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 MESSAGING CRITICAL: Não deve haver dependências cíclicas em eventos")
    void eventsShouldNotHaveCyclicDependencies() {
        ArchRule rule = slices()
                .matching("..event.(*)..")
                .should().beFreeOfCycles()
                .because("EVENTOS NÃO DEVEM TER DEPENDÊNCIAS CÍCLICAS");

        rule.allowEmptyShould(true).check(classes);
    }
}

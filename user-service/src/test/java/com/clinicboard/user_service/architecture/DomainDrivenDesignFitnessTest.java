package com.clinicboard.user_service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * 🧬 FITNESS FUNCTIONS para Domain-Driven Design
 * 
 * Baseado nos princípios de Vlad Khononov em "Learning Domain-Driven Design"
 * e Eric Evans em "Domain-Driven Design: Tackling Complexity in the Heart of Software"
 * 
 * Detecta violações de:
 * - Aggregate Roots
 * - Value Objects  
 * - Domain Services
 * - Domain Events
 * - Ubiquitous Language
 * - Bounded Contexts
 * - Anti-Corruption Layers
 */
@DisplayName("🔥 DOMAIN-DRIVEN DESIGN - Fitness Functions")
class DomainDrivenDesignFitnessTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.clinicboard.user_service");
    }

    // ==================== VALUE OBJECTS ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Value Objects devem ser imutáveis")
    void valueObjectsMustBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveNameMatching(".*Email|.*Password|.*ContactInfo|.*UserId")
                .should().haveOnlyFinalFields()
                .because("VALUE OBJECTS DEVEM SER IMUTÁVEIS - DDD PRINCÍPIO FUNDAMENTAL");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Value Objects não devem ter setters")
    void valueObjectsMustNotHaveSetters() {
        ArchRule rule = noMethods()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.model..")
                .and().areDeclaredInClassesThat().haveNameMatching(".*Email|.*Password|.*ContactInfo|.*UserId")
                .should().haveNameMatching("set.*")
                .because("VALUE OBJECTS SÃO IMUTÁVEIS - NÃO DEVEM TER SETTERS");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Value Objects devem estar no domínio")
    void valueObjectsMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Email|.*Password|.*ContactInfo|.*UserId")
                .should().resideInAPackage("..domain.model..")
                .because("VALUE OBJECTS PERTENCEM AO DOMÍNIO");

        rule.check(classes);
    }

    // ==================== DOMAIN SERVICES ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domain Services devem estar no domínio")
    void domainServicesMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*DomainService")
                .should().resideInAPackage("..domain.service..")
                .because("DOMAIN SERVICES PERTENCEM AO DOMÍNIO");

        rule.check(classes);
    }

    // ==================== DOMAIN EVENTS ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domain Events devem estar no domínio")
    void domainEventsMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Event|.*DomainEvent")
                .should().resideInAPackage("..domain.event..")
                .because("DOMAIN EVENTS PERTENCEM AO DOMÍNIO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domain Events devem ser imutáveis")
    void domainEventsMustBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .should().haveOnlyFinalFields()
                .because("DOMAIN EVENTS DEVEM SER IMUTÁVEIS");

        rule.check(classes);
    }

    // ==================== BUSINESS EXCEPTIONS ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Business Exceptions devem estar no domínio")
    void businessExceptionsMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*BusinessException")
                .should().resideInAPackage("..domain.exception..")
                .because("BUSINESS EXCEPTIONS REPRESENTAM VIOLAÇÕES DE REGRAS DE NEGÓCIO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Business Exceptions devem herdar de RuntimeException")
    void businessExceptionsMustInheritFromRuntimeException() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.exception..")
                .should().beAssignableTo(RuntimeException.class)
                .because("BUSINESS EXCEPTIONS DEVEM SER UNCHECKED");

        rule.check(classes);
    }

    // ==================== UBIQUITOUS LANGUAGE ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Classes do domínio devem usar linguagem ubíqua")
    void domainClassesMustUseUbiquitousLanguage() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain..")
                .should().haveNameNotMatching(".*Data|.*Info|.*Dto|.*Entity|.*Model")
                .because("DOMÍNIO DEVE USAR LINGUAGEM UBÍQUA - NÃO TERMOS TÉCNICOS");

        rule.check(classes);
    }

    // ==================== BOUNDED CONTEXT INTEGRITY ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domínio não pode importar outros bounded contexts")
    void domainMustNotImportOtherBoundedContexts() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "..business_service..",
                    "..notification_service..",
                    "..gateway.."
                )
                .because("BOUNDED CONTEXTS DEVEM SER INDEPENDENTES");

        rule.check(classes);
    }

    // ==================== ANTI-PATTERNS ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Value Objects não devem ter identidade")
    void valueObjectsMustNotHaveIdentity() {
        ArchRule rule = noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.model..")
                .and().areDeclaredInClassesThat().haveNameMatching(".*Email|.*Password|.*ContactInfo")
                .should().haveNameMatching(".*id.*|.*Id")
                .because("VALUE OBJECTS NÃO DEVEM TER IDENTIDADE");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domínio deve ser o centro da aplicação")
    void domainMustBeTheCenterOfApplication() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..domain..")
                .because("DOMÍNIO DEVE SER O CENTRO - HEXAGONAL + DDD");

        rule.check(classes);
    }

    // ==================== AGGREGATE DESIGN ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Aggregate Roots devem estar no domínio")
    void aggregateRootsMustBeInDomain() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*User.*") // User é nosso Aggregate Root
                .and().areNotInterfaces()
                .should().resideInAPackage("..domain.model..")
                .because("AGGREGATE ROOTS PERTENCEM AO DOMÍNIO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Application Exceptions devem estar na aplicação")
    void applicationExceptionsMustBeInApplication() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ApplicationException")
                .should().resideInAPackage("..application.exception..")
                .because("APPLICATION EXCEPTIONS PERTENCEM À CAMADA DE APLICAÇÃO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 DDD CRITICAL: Infrastructure não deve vazar para o domínio")
    void infrastructureMustNotLeakIntoDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .because("INFRAESTRUTURA NÃO DEVE VAZAR PARA O DOMÍNIO");

        rule.check(classes);
    }

    // ==================== CONSISTENCY & VALIDATION ====================

    @Test
    @DisplayName("🚨 DDD CRITICAL: Domínio deve validar usando suas próprias exceções")
    void domainMustValidateUsingOwnExceptions() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application.exception..")
                .because("DOMÍNIO DEVE SER INDEPENDENTE DA APLICAÇÃO");

        rule.check(classes);
    }
}

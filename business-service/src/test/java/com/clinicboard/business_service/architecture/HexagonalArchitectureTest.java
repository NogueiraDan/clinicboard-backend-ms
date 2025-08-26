package com.clinicboard.business_service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Fitness Functions para validar aderência à Arquitetura Hexagonal
 * 
 * Garante que as camadas sejam respeitadas e que o domínio permaneça puro
 */
@DisplayName("Hexagonal Architecture - Fitness Functions")
class HexagonalArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
            .importPackages("com.clinicboard.business_service");
    }

    @Nested
    @DisplayName("Separação de Camadas")
    class LayerSeparation {

        @Test
        @DisplayName("Deve respeitar a separação entre camadas da arquitetura hexagonal")
        void shouldRespectHexagonalLayerSeparation() {
            ArchRule layerRule = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                
                // Definição das camadas
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                
                // Regras de dependência
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain");

            layerRule.check(classes);
        }

        @Test
        @DisplayName("Domínio não deve depender de infraestrutura")
        void domainShouldNotDependOnInfrastructure() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domínio não deve depender de application")
        void domainShouldNotDependOnApplication() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application..");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domínio não deve depender do Spring Framework")
        void domainShouldNotDependOnSpring() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "javax.persistence..", "jakarta.persistence..");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Estrutura de Packages")
    class PackageStructure {

        @Test
        @DisplayName("Classes de domínio devem estar no package correto")
        void domainClassesShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .should().bePublic();

            rule.check(classes);
        }

        @Test
        @DisplayName("Exceções de domínio devem estar no package correto")
        void domainExceptionsShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.exception..")
                .should().beAssignableTo(RuntimeException.class);

            rule.check(classes);
        }

        @Test
        @DisplayName("Eventos de domínio devem estar no package correto")
        void domainEventsShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .should().beInterfaces()
                .orShould().beRecords();

            rule.check(classes);
        }

        @Test
        @DisplayName("Portas de entrada devem estar no package correto")
        void inputPortsShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application.port.in..")
                .should().beInterfaces();

            rule.check(classes);
        }

        @Test
        @DisplayName("Portas de saída devem estar no package correto")
        void outputPortsShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application.port.out..")
                .should().beInterfaces();

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Naming Conventions")
    class NamingConventions {

        @Test
        @DisplayName("Casos de uso devem terminar com UseCase")
        void useCasesShouldEndWithUseCase() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application.usecase..")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("UseCase")
                .orShould().haveSimpleNameEndingWith("UseCaseImpl");

            rule.check(classes);
        }

        @Test
        @DisplayName("Portas de entrada devem terminar com InputPort ou Command/Query")
        void inputPortsShouldFollowNamingConvention() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application.port.in..")
                .should().haveSimpleNameEndingWith("InputPort")
                .orShould().haveSimpleNameEndingWith("Command")
                .orShould().haveSimpleNameEndingWith("Query");

            rule.check(classes);
        }

        @Test
        @DisplayName("Portas de saída devem terminar com OutputPort ou Repository/Gateway")
        void outputPortsShouldFollowNamingConvention() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application.port.out..")
                .should().haveSimpleNameEndingWith("OutputPort")
                .orShould().haveSimpleNameEndingWith("Repository")
                .orShould().haveSimpleNameEndingWith("Gateway");

            rule.check(classes);
        }

        @Test
        @DisplayName("Exceções de domínio devem terminar com Exception")
        void domainExceptionsShouldEndWithException() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.exception..")
                .should().haveSimpleNameEndingWith("Exception");

            rule.check(classes);
        }

        @Test
        @DisplayName("Eventos de domínio devem terminar com Event")
        void domainEventsShouldEndWithEvent() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("Event");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Dependency Rules")
    class DependencyRules {

        @Test
        @DisplayName("Adaptadores de entrada devem depender apenas de portas de entrada")
        void inputAdaptersShouldOnlyDependOnInputPorts() {
            ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter.in..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application.port.in..", "..domain..", "..infrastructure.config..")
                .orShould().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "javax.validation..", "jakarta.validation..");

            rule.check(classes);
        }

        @Test
        @DisplayName("Adaptadores de saída devem implementar portas de saída")
        void outputAdaptersShouldImplementOutputPorts() {
            ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter.out..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application.port.out..", "..domain..", "..infrastructure.config..");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Domain Purity")
    class DomainPurity {

        @Test
        @DisplayName("Value Objects devem ser records ou classes imutáveis")
        void valueObjectsShouldBeRecordsOrImmutable() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameNotEndingWith("Service")
                .and().haveSimpleNameNotEndingWith("Exception")
                .should().beRecords()
                .orShould().haveOnlyFinalFields();

            rule.check(classes);
        }

        @Test
        @DisplayName("Agregados devem ter métodos de negócio públicos")
        void aggregatesShouldHavePublicBusinessMethods() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .and().areNotRecords()
                .and().areNotEnums()
                .should().bePublic();

            rule.check(classes);
        }

        @Test
        @DisplayName("Serviços de domínio devem terminar com DomainService")
        void domainServicesShouldEndWithDomainService() {
            ArchRule rule = classes()
                .that().resideInAPackage("..domain.service..")
                .should().haveSimpleNameEndingWith("DomainService");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Anti-Patterns Detection")
    class AntiPatternsDetection {

        @Test
        @DisplayName("Não deve haver dependências circulares")
        void shouldNotHaveCircularDependencies() {
            ArchRule rule = slices()
                .matching("com.clinicboard.business_service.(*)..")
                .should().beFreeOfCycles();

            rule.check(classes);
        }

        @Test
        @DisplayName("Classes de domínio não devem usar anotações de persistência")
        void domainClassesShouldNotUsePersistenceAnnotations() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("javax.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("org.springframework.data.annotation.Id");

            rule.check(classes);
        }

        @Test
        @DisplayName("Classes de domínio não devem usar anotações do Spring")
        void domainClassesShouldNotUseSpringAnnotations() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository");

            rule.check(classes);
        }
    }
}

package com.clinicboard.user_service.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * 🧬 FITNESS FUNCTIONS para Anti-Patterns
 * 
 * Detecta violações de anti-patterns comuns em:
 * - God Classes/Objects
 * - Anemic Domain Models
 * - Circular Dependencies
 * - Feature Envy
 * - Data Classes
 * - Tight Coupling
 * - Violation of Single Responsibility
 */
@DisplayName("🔥 ANTI-PATTERNS - Fitness Functions")
class AntiPatternsFitnessTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.clinicboard.user_service");
    }

    // ==================== ANEMIC DOMAIN MODEL ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Domain Models devem ter comportamentos")
    void domainModelsShouldHaveBehaviors() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .and().areNotEnums()
                .and().areNotInterfaces()
                .and().haveNameNotMatching(".*Id|.*Email|.*Password") // Value Objects podem ser simples
                .should().haveOnlyFinalFields()
                .because("DOMAIN MODELS DEVEM TER COMPORTAMENTOS DE NEGÓCIO");

        rule.check(classes);
    }

    // ==================== CIRCULAR DEPENDENCIES ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Detectar dependências cíclicas no domínio")
    void detectCircularDependenciesInDomain() {
        ArchRule rule = slices()
                .matching("..domain.(*)..")
                .should().beFreeOfCycles()
                .because("DEPENDÊNCIAS CÍCLICAS VIOLAM DESIGN LIMPO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Detectar dependências cíclicas na aplicação")
    void detectCircularDependenciesInApplication() {
        ArchRule rule = slices()
                .matching("..application.(*)..")
                .should().beFreeOfCycles()
                .because("DEPENDÊNCIAS CÍCLICAS VIOLAM DESIGN LIMPO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Detectar dependências cíclicas na infraestrutura")
    void detectCircularDependenciesInInfrastructure() {
        ArchRule rule = slices()
                .matching("..infrastructure.(*)..")
                .should().beFreeOfCycles()
                .because("DEPENDÊNCIAS CÍCLICAS VIOLAM DESIGN LIMPO");

        rule.check(classes);
    }

    // ==================== TIGHT COUPLING ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Domain não deve depender de muitos packages externos")
    void domainShouldNotDependOnManyExternalPackages() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "..domain..",
                    "java..",
                    "javax..",
                    "jakarta.."
                )
                .because("DOMAIN DEVE TER BAIXO ACOPLAMENTO EXTERNO");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Casos de uso não devem depender de outros casos de uso")
    void useCasesShouldNotDependOnOtherUseCases() {
        ArchRule rule = noClasses()
                .that().haveNameMatching(".*UseCaseImpl")
                .should().dependOnClassesThat().haveNameMatching(".*UseCaseImpl")
                .because("CASOS DE USO DEVEM SER INDEPENDENTES");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Controllers não devem depender de outros controllers")
    void controllersShouldNotDependOnOtherControllers() {
        ArchRule rule = noClasses()
                .that().haveNameMatching(".*Controller")
                .should().dependOnClassesThat().haveNameMatching(".*Controller")
                .because("CONTROLLERS DEVEM SER INDEPENDENTES");

        rule.check(classes);
    }

    // ==================== VIOLATION OF SRP ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Evitar classes 'Utils' no domínio")
    void avoidUtilClassesInDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().haveNameMatching(".*Util|.*Helper|.*Manager")
                .because("DOMAIN DEVE TER CLASSES COM RESPONSABILIDADES CLARAS");

        rule.check(classes);
    }

    // ==================== INAPPROPRIATE INTIMACY ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Packages não devem ter intimidade inapropriada")
    void packagesShouldNotHaveInappropriateIntimacy() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().accessClassesThat().resideInAPackage("..infrastructure..")
                .because("APPLICATION NÃO DEVE ACESSAR DETALHES DE INFRAESTRUTURA");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Value Objects não devem depender uns dos outros")
    void valueObjectsShouldNotDependOnEachOther() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain.model..")
                .and().haveNameMatching(".*Email|.*Password|.*ContactInfo|.*UserId")
                .should().dependOnClassesThat().haveNameMatching(".*Email|.*Password|.*ContactInfo|.*UserId")
                .because("VALUE OBJECTS DEVEM SER INDEPENDENTES");

        rule.check(classes);
    }

    // ==================== PRIMITIVE OBSESSION ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Domínio deve usar Value Objects ao invés de primitivos")
    void domainShouldUseValueObjectsInsteadOfPrimitives() {
        ArchRule rule = noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..domain.model..")
                .and().areDeclaredInClassesThat().haveNameMatching(".*User.*") // Aggregate roots
                .should().haveRawType(String.class)
                .because("DOMAIN DEVE USAR VALUE OBJECTS - NÃO PRIMITIVOS");

        rule.check(classes);
    }

    // ==================== SHOTGUN SURGERY ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Configurações devem estar centralizadas")
    void configurationsShouldBeCentralized() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.context.annotation.Configuration")
                .should().resideInAPackage("..infrastructure.config..")
                .because("CONFIGURAÇÕES DEVEM ESTAR CENTRALIZADAS");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Exception handling deve estar padronizado")
    void exceptionHandlingShouldBeStandardized() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ExceptionHandler|.*ErrorHandler")
                .should().resideInAPackage("..infrastructure..")
                .because("EXCEPTION HANDLING DEVE ESTAR PADRONIZADO");

        rule.allowEmptyShould(true).check(classes);
    }

    // ==================== DUPLICATE CODE ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Evitar duplicate interfaces")
    void avoidDuplicateInterfaces() {
        ArchRule rule = classes()
                .that().areInterfaces()
                .and().resideInAPackage("..application.port..")
                .should().haveNameNotMatching(".*Repository.*")
                .because("EVITAR DUPLICAÇÃO DE CONTRATOS");

        rule.check(classes);
    }

    // ==================== DATA CLASSES ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Records devem estar nas camadas corretas")
    void recordsShouldBeInCorrectLayers() {
        ArchRule rule = classes()
                .that().areRecords()
                .should().resideInAnyPackage(
                    "..application.port.in..",
                    "..infrastructure.adapter..",
                    "..domain.model.."
                )
                .because("RECORDS DEVEM ESTAR EM CAMADAS APROPRIADAS");

        rule.check(classes);
    }

    // ==================== NAMING ANTI-PATTERNS ====================

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Evitar nomes genéricos")
    void avoidGenericNames() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().haveNameMatching(".*Data|.*Info|.*Manager|.*Handler")
                .because("NOMES GENÉRICOS VIOLAM LINGUAGEM UBÍQUA");

        rule.check(classes);
    }

    @Test
    @DisplayName("🚨 ANTI-PATTERN: Classes não devem ter prefixos técnicos")
    void classesShouldNotHaveTechnicalPrefixes() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().haveNameMatching("Abstract.*|Base.*|Generic.*")
                .because("PREFIXOS TÉCNICOS VIOLAM LINGUAGEM UBÍQUA");

        rule.check(classes);
    }
}

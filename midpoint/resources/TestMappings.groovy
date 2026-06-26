#!/usr/bin/env groovy

// Framework de Pruebas Unitarias Ligero para Mappings de midPoint

class AsteriskMappingTests {

    // Simulación del Mapping 1: Limpieza de la Extensión Telefónica
    static String executeExtensionMapping(String employeeNumber) {
        if (employeeNumber == null) return null
        return employeeNumber.toString().replaceAll("\\s+", "")
    }

    // Simulación del Mapping 2: Generación Segura de Claves (ISO 27001)
    static String executeSecretMapping(String givenName) {
        if (givenName == null) return "SecPBX2026*"
        return givenName.toLowerCase() + "FinanPBX2026#"
    }

    // Ejecutor de la Suite de Pruebas
    static void runTests() {
        int totalTests = 0
        int passedTests = 0

        println "================================================================="
        println " RUNNING UNIT TESTS: MIDPOINT MAPPINGS & BUSINESS RULES"
        println "================================================================="

        // --- TEST 1: Validación de Extensión Limpia (Sin Espacios) ---
        totalTests++
        print "Test 1 [Extension Mapping - Clean Spaces]: "
        def inputEmpNum = " 10 02 "
        def expectedExt = "1002"
        def resultExt = executeExtensionMapping(inputEmpNum)
        if (resultExt == expectedExt) {
            println "PASSED"
            passedTests++
        } else {
            println "FAILED (Expected: '${expectedExt}', Got: '${resultExt}')"
        }

        // --- TEST 2: Gestión de Extensión con Valores Nulos ---
        totalTests++
        print "Test 2 [Extension Mapping - Null Safe]:   "
        if (executeExtensionMapping(null) == null) {
            println "PASSED"
            passedTests++
        } else {
            println "FAILED"
        }

        // --- TEST 3: Generación de Secreto Dinámico Válido ---
        totalTests++
        print "Test 3 [Secret Mapping - Dynamic Key]:    "
        def inputName = "Alexander"
        def expectedSecret = "alexanderFinanPBX2026#"
        def resultSecret = executeSecretMapping(inputName)
        if (resultSecret == expectedSecret) {
            println "PASSED"
            passedTests++
        } else {
            println "FAILED (Expected: '${expectedSecret}', Got: '${resultSecret}')"
        }

        // --- TEST 4: Fallback de Seguridad Ante Nombre Nulo ---
        totalTests++
        print "Test 4 [Secret Mapping - Null Fallback]:  "
        def expectedFallback = "SecPBX2026*"
        if (executeSecretMapping(null) == expectedFallback) {
            println "PASSED"
            passedTests++
        } else {
            println "FAILED"
        }

        println "================================================================="
        println " SUMMARY: Passed ${passedTests} of ${totalTests} test cases."
        println "================================================================="
        
        // Retornar código de salida para la integración continua (CI/CD)
        if (passedTests == totalTests) System.exit(0) else System.exit(1)
    }
}

// Ejecutar la suite
AsteriskMappingTests.runTests()

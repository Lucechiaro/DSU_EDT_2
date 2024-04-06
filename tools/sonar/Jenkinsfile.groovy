def scannerHome
def PROJECT_KEY
def SRC = ''
def PROJECT_NAME_EDT = ''
def config = [
        "versionFilePath":'src/VERSION'
    ]
def scanner_properties    

   

pipeline {
   

    agent { label 'slave' }

    options {
        /*timeout(time: 8, unit: TimeUnit.HOURS)
        buildDiscarder(logRotator(numToKeepStr: '10'))*/
        timestamps()
    }

    stages {
       
        stage("Init") {
            steps {
                script {

                    // Инициализация параметров значениями по умолчанию
                    /*sonar_catalog = commonMethods.initParam(sonar_catalog, 'C:/Sonar/', 'sonar_catalog')*/
                    PROJECT_NAME = "DSU"
                    SRC = "./ДСУтест/src/"
                    PROJECT_KEY = PROJECT_NAME
                    /*PROPERTIES_CATALOG = commonMethods.initParam(PROPERTIES_CATALOG, './Sonar', 'PROPERTIES_CATALOG')

                    STEBI_SETTINGS = commonMethods.initParam(STEBI_SETTINGS, './Sonar/settings.json', 'STEBI_SETTINGS')
                    git_repo_branch = commonMethods.initParam(git_repo_branch, 'master', 'git_repo_branch')

                    JobWithCover = JobWithCover == null || JobWithCover == 'null' ? '' : JobWithCover
                    EDT_VERSION = commonMethods.initParam(EDT_VERSION, '2020.6.0', 'EDT_VERSION')

                    BIN_CATALOG = "${sonar_catalog}/bin/"
                    ACC_BASE = "${sonar_catalog}/ACC/"
                    ACC_USER = 'Admin'
                    

                    // Подготовка переменных по переданным параметрам
                    // Настройки инструментов
                    ACC_PROPERTIES = "./Repo/${PROPERTIES_CATALOG}/acc.properties"
                    if (fileExists(ACC_PROPERTIES)) {
                        commonMethods.log("file exists: ${ACC_PROPERTIES}")
                    } else {
                        commonMethods.log("file does not exist: ${ACC_PROPERTIES}")
                        ACC_PROPERTIES = './Sonar/acc.properties'
                    }

                    // csv файл с параметрами ишузов для выгрузки АПК
                    ACC_CLASSIFICATION_ERROR = "./Repo/${PROPERTIES_CATALOG}/acc.ClassificationError.csv"
                    if (fileExists(ACC_CLASSIFICATION_ERROR)) {
                        commonMethods.log("file exists: ${ACC_CLASSIFICATION_ERROR}")
                        } else {
                        commonMethods.log("file does not exist: ${ACC_CLASSIFICATION_ERROR}")
                        ACC_CLASSIFICATION_ERROR = './Sonar/acc.ClassificationError.csv'
                    }

                    CURRENT_CATALOG = pwd()
                    TEMP_CATALOG = "${CURRENT_CATALOG}\\sonar_temp"
                    EDT_VALIDATION_RESULT = "${TEMP_CATALOG}\\edt-result.csv"
                    CURRENT_CATALOG = "${CURRENT_CATALOG}\\Repo"

                    // создаем/очищаем временный каталог
                    dir(TEMP_CATALOG) {
                        deleteDir()
                        writeFile file: 'acc.json', text: '{"issues": [], "rules": []}'
                        writeFile file: 'edt.json', text: '{"issues": [], "rules": []}'
                    }
                    PROJECT_NAME_EDT = "${CURRENT_CATALOG}\\${PROJECT_NAME}"
                    if (git_repo_branch == 'master') {
                        PROJECT_KEY = PROJECT_NAME
                    } else {
                        PROJECT_KEY = "${PROJECT_NAME}_${git_repo_branch}"
                    }

                    

                    GENERIC_ISSUE_JSON = "${TEMP_CATALOG}/acc.json,${TEMP_CATALOG}/edt.json"
                */
                }
                
            }
        }

        /*stage('Checkout') {
            steps {
                script {
                    dir('Repo') {
                        commonMethods.cmd("git lfs install")
                        checkout([$class: 'GitSCM',
                            branches: [[name: "${git_repo_branch}"]],
                            browser: [$class: 'GitLab', repoUrl: git_repo_url],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [[$class: 'CheckoutOption', timeout: 60], [$class: 'GitLFSPull'], [$class: 'CleanBeforeCheckout', deleteUntrackedNestedRepositories: true], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: false, timeout: 60]],
                            userRemoteConfigs: [[credentialsId: scm.userRemoteConfigs[0].credentialsId, url: "${git_repo_url}"]]])
                    }
                }
            }
        }
        */

        stage('Sonar scanning') {
                      

            steps {
               
                script {
                    scannerHome = tool 'sonar-scanner'
                    /*def configurationText = readFile(encoding: 'UTF-8', file: config.versionFilePath)
                    configurationVersion = (configurationText =~ /<VERSION>(.*)<\/VERSION>/)[0][1] 
                    scannerHome = scannerHome.replaceAll("\\s","\\ ")*/

                    scanner_properties = ["-X",
                                /*"-Dsonar.projectVersion=%SONAR_PROJECTVERSION%",*/
                                "-Dsonar.projectKey=${PROJECT_KEY}",
                                "-Dsonar.sources=\"${SRC}\"",
                                /*"-Dsonar.externalIssuesReportPaths=${GENERIC_ISSUE_JSON}",*/
                                '-Dsonar.sourceEncoding=UTF-8',
                                '-Dsonar.inclusions=**/*.bsl',
                                '-Dsonar.bsl.languageserver.enabled=true',
                                '-Dfile.encoding=UTF-8',
                                "-Dsonar.token="${token}""
                            ].join(' ')


                }

                withSonarQubeEnv('Sonar') {

                    /*cmd('D:\\\"Portable Software\"\\SonarScanner\\bin\\sonar-scanner -D sonar.projectVersion=' + "${configurationVersion}")*/
                    cmd("@echo ${scanner_properties}")
                    cmd("""${scannerHome}\\bin\\sonar-scanner ${scanner_properties} """)
                }
            }
        }

    }
}

def cmd(command) {
    bat "chcp 65001 > nul && ${command}"
}

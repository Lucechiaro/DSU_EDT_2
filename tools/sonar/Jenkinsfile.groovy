def scannerHome
def PROJECT_KEY
def SRC = ''
def PROJECT_NAME_EDT = ''
def config = [
        "versionFilePath":'src/VERSION'
    ]

   

pipeline {
   
    parameters {
        string(defaultValue: "${env.PROJECT_NAME}", description: '* Имя проекта. Одинаковое для EDT, проекта в АПК и в сонаре. Обычно совпадает с именем конфигурации.', name: 'PROJECT_NAME')
        /*string(defaultValue: "${env.git_repo_url}", description: '* URL к гит-репозиторию, который необходимо проверить.', name: 'git_repo_url')
        string(defaultValue: "${env.git_repo_branch}", description: 'Ветка репозитория, которую необходимо проверить. По умолчанию master', name: 'git_repo_branch')
        string(defaultValue: "${env.sonar_catalog}", description: 'Каталог сонара, в котором лежит все, что нужно. По умолчанию C:/Sonar/', name: 'sonar_catalog')
        string(defaultValue: "${env.PROPERTIES_CATALOG}", description: 'Каталог с настройками acc.properties sonar-project.properties. По умолчанию ./Sonar', name: 'PROPERTIES_CATALOG')
        booleanParam(defaultValue: env.ACC_check == null ? true : env.ACC_check, description: 'Выполнять ли проверку АПК. Если нет, то будут получены существующие результаты. По умолчанию: true', name: 'ACC_check')
        booleanParam(defaultValue: env.ACC_recreateProject == null ? false : env.ACC_recreateProject, description: 'Пересоздать проект в АПК. Все данные о проекте будут собраны заново. По умолчанию: false', name: 'ACC_recreateProject')
        string(defaultValue: "${env.STEBI_SETTINGS}", description: 'Файл настроек для переопределения замечаний. Для файла из репо проекта должен начинатся с папки Repo, например .Repo/Sonar/settings.json. По умолчанию ./Sonar/settings.json', name: 'STEBI_SETTINGS')
        string(defaultValue: "${env.jenkinsAgent}", description: 'Нода дженкинса, на которой запускать пайплайн. По умолчанию master', name: 'jenkinsAgent')
        string(defaultValue: "${env.JobWithCover}", description: 'Имя джоба с тестами в котором был выполнен сбор покрытия тестами', name: 'JobWithCover')
        string(defaultValue: "${env.EDT_VERSION}", description: 'Используемая версия EDT. По умолчанию 2020.6.0', name: 'EDT_VERSION')
        string(defaultValue: "${env.rocket_channel}", description: 'Канал в рокет-чате для отправки уведомлений', name: 'rocket_channel')
        booleanParam(defaultValue: env.debug, description: 'Вывод отладочных сообщений.', name: 'debug')*/
    }

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
                    SRC = "./src"
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

        stage('Checkout') {
            steps {
                script {
                    dir('Repo') {
                        /*commonMethods.cmd("git lfs install")*/
                        checkout([$class: 'GitSCM',
                            branches: [[name: "*/${git_repo_branch}"]],
                            browser: [$class: 'GitLab', repoUrl: git_repo_url],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [[$class: 'CheckoutOption', timeout: 60], [$class: 'GitLFSPull'], [$class: 'CleanBeforeCheckout', deleteUntrackedNestedRepositories: true], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: false, timeout: 60]],
                            userRemoteConfigs: [[credentialsId: scm.userRemoteConfigs[0].credentialsId, url: "${git_repo_url}"]]])
                    }
                }
            }
        }

        stage('Sonar scanning') {
            
            steps {
               
                script {
                    scannerHome = tool 'sonar-scanner'
                    /*def configurationText = readFile(encoding: 'UTF-8', file: config.versionFilePath)
                    configurationVersion = (configurationText =~ /<VERSION>(.*)<\/VERSION>/)[0][1] 
                    scannerHome = scannerHome.replaceAll("\\s","\\ ")*/
                }
               
                withSonarQubeEnv('Sonar') {

                     def scanner_properties = [
                                "-X",
                                /*"-Dsonar.projectVersion=%SONAR_PROJECTVERSION%",*/
                                "-Dsonar.projectKey=${PROJECT_KEY}",
                                "-Dsonar.sources=\"${SRC}\"",
                                /*"-Dsonar.externalIssuesReportPaths=${GENERIC_ISSUE_JSON}",*/
                                "-Dsonar.sourceEncoding=UTF-8",
                                "-Dsonar.inclusions=**/*.bsl",
                                "-Dsonar.bsl.languageserver.enabled=true",
                                "-Dfile.encoding=UTF-8"
                            ].join(' ')

                    /*cmd('D:\\\"Portable Software\"\\SonarScanner\\bin\\sonar-scanner -D sonar.projectVersion=' + "${configurationVersion}")*/
                    cmd("""
                                @set SRC=\"${SRC}\"
                                @echo %SRC%
                                @set SONAR_SCANNER_OPTS=-Xmx6g
                                ${scannerHome}\\bin\\sonar-scanner ${scanner_properties}
                                """)
                }

            }
        }

    }
}

def cmd(command) {
    bat "chcp 65001 > nul && ${command}"
}

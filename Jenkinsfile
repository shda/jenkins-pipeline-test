@Library('unity') _

pipeline {
    parameters {
        choice name: 'NODE', choices: ['unity', 'win', 'mac'], description: 'Node'
        booleanParam name: 'AUTO_DETECT_UNITY_VERSION', defaultValue: true
        string name: 'UNITY_VERSION', defaultValue: '2021.1.28f1'
        string name: 'UNITY_VERSION_REVISION', defaultValue: ''

        text name: 'BUILD_SCENES', defaultValue: ''
        text name: 'EXTRA_SCRIPTING_DEFINES', defaultValue: ''
        string name: 'PRE_BUILD_METHOD', defaultValue: ''
        string name: 'POST_BUILD_METHOD', defaultValue: ''

    }

    agent {
        label params.NODE
    }

    stages {
//        stage('Setup parameters') {
//            steps {
//                script {
//                    unityHub.setupJobParameters()
//                }
//            }
//        }
        stage('Checkout') {
            steps {
                git branch: 'dev',
                        credentialsId: 'bitbucket',
                        url: 'https://bitbucket.org/prosaas/cryptotanks-unity.git'
            }
        }
        stage('Build') {
            steps {
                script {
                    def autoDetectUnityVersion = params.AUTO_DETECT_UNITY_VERSION
                    def (projUnityVersion, projUnityRevision) = unityUtils.getProjectUnityVersionAndRevision('.')
                    unityHub.init(UNITY_HUB_PATH)
                    def unityVersion = autoDetectUnityVersion && projUnityVersion ? projUnityVersion : param.UNITY_VERSION
                    def unityRevision = autoDetectUnityVersion && projUnityRevision ? projUnityRevision : param.UNITY_VERSION_REVISION
                    log.info("required unityVersion: ${unityVersion}")
                    log.info("required unityRevision: ${unityRevision}")
                    def unityPath = unityHub.getUnityPath(unityVersion, unityRevision, false)

                    buildOptions = new HashMap<String, Object>()

                    if (buildOptions) {
                        buildOptions['scenes'] = param.BUILD_SCENES
                    }
                    if (buildOptions) {
                        buildOptions['extraScriptingDefines'] = param.EXTRA_SCRIPTING_DEFINES
                    }
                    if (buildOptions) {
                        buildOptions['preBuildMethod'] = param.PRE_BUILD_METHOD
                    }
                    if (buildOptions) {
                        buildOptions['postBuildMethod'] = param.POST_BUILD_METHOD
                    }

                    def buildTarget = 'StandaloneWindows64'
                    buildOptions['locationPathName'] = '.Build/Win/game.exe'

                    buildOptions['buildTarget'] = buildTarget

                    writeJSON file: '.build_options.json', json: buildOptions
                    def additionalParameters = '-ciOptionsFile .build_options.json'


                    unity.init(unityPath)
                    unity.execute(projectDir: '.', methodToExecute: 'JenkinsBuilder.Build', buildTarget: buildTarget, additionalParameters: additionalParameters)
                }
            }
        }
        stage('Zip') {
            steps {
                script {
                    def buildArchivePath = "${BUILD_TAG}.zip"
                    zip zipFile: buildArchivePath, dir: '.Build/Win', overwrite: true, archive: true
                }
            }
        }
    }
}

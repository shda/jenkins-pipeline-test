@Library('unity') _

pipeline {
    parameters {
        choice name: 'NODE', choices: ['', 'unity', 'win', 'mac'], description: 'Node'
        booleanParam name: 'AUTO_DETECT_UNITY_VERSION', defaultValue: true
        string name: 'UNITY_VERSION', defaultValue: '2021.1.28f1'
//        string name: 'UNITY_REVISION', defaultValue: ''

//        text name: 'BUILD_SCENES', defaultValue: ''
//        text name: 'EXTRA_SCRIPTING_DEFINES', defaultValue: ''
//        string name: 'PRE_BUILD_METHOD', defaultValue: ''
//        string name: 'POST_BUILD_METHOD', defaultValue: ''
    }

    agent {
        label(params.NODE)
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
                git branch: env.BRANCH,
                        credentialsId: 'bitbucket',
                        url: 'https://bitbucket.org/prosaas/cryptotanks-unity.git'
            }
        }
        stage('Build') {
            steps {
                script {
                    unityBuilder.build(
                            unityHubPath: UNITY_HUB_PATH,
                            autoDetectUnityVersion: params.AUTO_DETECT_UNITY_VERSION,
                            unityVersion: param.UNITY_VERSION,
                            unityRevision: param.UNITY_REVISION,
                            projectDir: '.',
                            buildTarget: 'StandaloneWindows64',
                            locationPathName: '.Build/Win/game.exe',
                            scenes: param.SCENES,
                            extraScriptingDefines: param.EXTRA_SCRIPTING_DEFINES,
                            preBuildMethod: param.PRE_BUILD_METHOD,
                            postBuildMethod: param.POST_BUILD_METHOD
                    )
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

В Jenkins установить плагины Pipeline , Pipeline Utility Steps

1. Библиотека скриптов для сборок находятся тут https://github.com/mihanvr/jenkins-pipeline-test
В Настройки Jenkins-> Global Pipeline Libraries добавить
Name = unity
Default version = master
Project Repository = https://github.com/mihanvr/jenkins-pipeline-test.git
Library Path (optional) = ./

2. В Nodes , в настройках каждого узле сборки добавляем

Переменные среды:
Имя = UNITY_HUB_PATH
Значение = C:\Program Files\Unity Hub\Unity Hub.exe (для мака /Applications/Unity Hub.app)

Метки:
win unity (для мака mac unity unity-pro)

Удалённая корневая директория:
(для мака) /Volumes/SSD/jenkins/workspace

3. Варианты скриптов для конкретных сборок есть в https://github.com/mihanvr/jenkins-pipeline-test/tree/master/templates

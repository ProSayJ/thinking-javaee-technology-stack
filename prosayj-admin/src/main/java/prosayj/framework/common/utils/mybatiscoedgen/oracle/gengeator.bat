@echo off 
	 @if exist src rd /s src;
	 mkdir src\main\resources;
	 mkdir src\main\java;
	 java -jar mybatis-generator-core-1.3.5.jar -configfile generatorConfig.xml -overwrite 
@pause 
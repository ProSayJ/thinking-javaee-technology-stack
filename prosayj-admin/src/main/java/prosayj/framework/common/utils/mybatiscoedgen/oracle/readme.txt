1：cmd到当前的目录下：
2：运行下面的指令
	java -jar mybatis-generator-core-1.3.5.jar -configfile generatorConfig.xml -overwrite

3：升级版：
	3.1：因为mybatis逆向dao的时候指定了项目的工作空间。即：targetProject="src/main/resources"，所以在逆向生成之前需要有src/main/resources这个目录存在；
	3.2：因为mybatis多次逆向生成dao层接口对应的xml映射文件是以追加的形式存在的，不是替换，逆向生成的*.java是替换没毛病。
	综上所述：
		每次在执行逆向命令生成之前需要把上次生成的文件删掉，同时创建空的src/main/resources目录结构来为下次逆向生成做准备

每次手动删除目录很麻烦，所以写了个doc的简单的批处理命令：

	 @if exist src rd /s src;//如果文件夹存在则递归删除

	 mkdir src\main\resources;//创建多级目录

	 mkdir src\main\java;//创建多级目录

	 java -jar mybatis-generator-core-1.3.5.jar -configfile generatorConfig.xml -overwrite //执行逆向生成
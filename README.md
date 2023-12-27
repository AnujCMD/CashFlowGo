<h1 align="center">CashFlowGo</h1>
<p align="center"><i>Record and Fetch your transactions on the Go, with CashFlowGo.</i></p>
<div align="center">
  <a href="https://github.com/AnujCMD/CashFlowGo/stargazers"><img src="https://img.shields.io/github/stars/AnujCMD/CashFlowGo" alt="Stars Badge"/></a>
<a href="https://github.com/AnujCMD/CashFlowGo/network/members"><img src="https://img.shields.io/github/forks/AnujCMD/CashFlowGo" alt="Forks Badge"/></a>
<a href="https://github.com/AnujCMD/CashFlowGo/pulls"><img src="https://img.shields.io/github/issues-pr/AnujCMD/CashFlowGo" alt="Pull Requests Badge"/></a>
<a href="https://github.com/AnujCMD/CashFlowGo/issues"><img src="https://img.shields.io/github/issues/AnujCMD/CashFlowGo" alt="Issues Badge"/></a>
<a href="https://github.com/AnujCMD/CashFlowGo/graphs/contributors"><img alt="GitHub contributors" src="https://img.shields.io/github/contributors/AnujCMD/CashFlowGo?color=2b9348"></a>
<a href="https://github.com/AnujCMD/CashFlowGo/blob/master/LICENSE"><img src="https://img.shields.io/github/license/AnujCMD/CashFlowGo?color=2b9348" alt="License Badge"/></a>
<h1 align="left">Heighted Awareness for CashFlowGo Application: </h1>
<p align="left">Database Transactions -  MongoDB supports Atomic Operations on Single Document. Each operation is atomic so we have Data Isolation here. Only performance is impacted whgen we are doing operations in multiple collection in MongoDB but here we are just using one collection</p>
<p align="left">Database Locks - MongoDB don't have locks but something like Optimized Concurrency Control when we just have to @Version to a field and it will take of it by not allowing any user to update if the version when saving is different with version present in db. </p>
<p align="left>Potential Race Conditions - I have introducted @Version which mongo used to eliminate/lower the risk of race conditions but I am also using Rest Template which is a blocking call so need to change that as well to WebClient to take full user of Reactive Pipeline. </p>
<p align="left">Limitations - As previously mention, Rest Template is the biggest bummer here as the Application is made into a reactive pipeline but we are using the RestTemplate which will block a thread per call and reactive flow will break. Second thing is the less optimal approach towards implementing the get method and aggregating the total INR/USD rupees of the day, this implementation can be smoothened. Also the unit test case are just for mongo save and get which can also be diversified.</p>
[CashFlowGo: Architecture](https://github.com/AnujCMD/CashFlowGo/files/13781706/Jar_CashFlowGo.pdf)

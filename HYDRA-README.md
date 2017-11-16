# How to use the benchmark on Triplequote Scala Hydra

Inside Sbt:

```
> set resolvers in ThisBuild  += Resolver.mavenLocal
> set scalaOrganization in ThisBuild := "com.triplequote"
> set scalaVersion in ThisBuild := "2.11.11-hydra17"
> hot -p source=@/Users/dragos/workspace/triplequote/customers/coursera/triplequote-repo/balrog.args
```

The last one uses an args file to run compilation on that project


Better files

```
[info] Benchmark                                 (extraArgs)      (source)    Mode  Cnt    Score   Error  Units
[info] HotScalacBenchmark.compile                               better-files  sample  497  622.966 ± 3.582  ms/op
[info] HotScalacBenchmark.compile                    -Ymetrics  better-files  sample  486  639.243 ± 3.360  ms/op

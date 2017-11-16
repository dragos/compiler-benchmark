package scala.tools.benchmark

import java.nio.file._
import scala.tools.nsc._

trait CompilerMainClass {
  def compile(args: Array[String]): Unit
  def compileResident(args: Array[String]): Unit
  def hasErrors: Boolean
}

trait BenchmarkDriver extends BaseBenchmarkDriver {
  private var driver: MainClass = _

  // MainClass is copy-pasted from compiler for source compatibility with 2.10.x - 2.13.x
  private class MainClass extends Driver with EvalLoop with CompilerMainClass {
    private var files: List[String] = _

    override def hasErrors: Boolean = reporter.hasErrors

    override def compile(args: Array[String]): Unit = process(args)

    override def compileResident(args: Array[String]): Unit = {
      if (compiler == null) {
        process(allArgs.toArray)
        val command = new CompilerCommand(allArgs, compiler.settings)
        files = command.files
      } else {
        val comp = compiler // needs to be a val in order to create a new Run off it
        compiler.reporter.reset()
        new comp.Run() compile files
      }
    }

    var compiler: Global = _
    override def newCompiler(): Global = {
      compiler = Global(settings, reporter)
      compiler
    }

    override protected def processSettingsHook(): Boolean = {
      if (source == "scala")
        settings.sourcepath.value = Paths.get(s"../corpus/$source/$corpusVersion/library").toAbsolutePath.normalize.toString
      else
        settings.usejavacp.value = true
      settings.outdir.value = tempDir.getAbsolutePath
      settings.nowarn.value = true
      if (depsClasspath != null)
        settings.processArgumentString(s"-cp $depsClasspath")
      true
    }
  }

  def compileImpl(): Unit = {
    if (isResident) {
      if (driver == null) driver = new MainClass
      driver.compileResident(allArgs.toArray)
    } else {
      driver = new MainClass
      driver.compile(allArgs.toArray)
    }
    assert(!driver.hasErrors)
  }

}

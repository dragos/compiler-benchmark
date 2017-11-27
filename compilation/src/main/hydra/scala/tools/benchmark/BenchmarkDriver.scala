package scala.tools.benchmark

import scala.tools.nsc.BaseBenchmarkDriver
import scala.tools.nsc.CompilerCommand
import scala.tools.nsc.Global
import scala.tools.nsc.reporters.Reporter

import com.triplequote.hydra.Main

trait BenchmarkDriver extends BaseBenchmarkDriver {
  def compileImpl(): Unit = {
    // this works starting with Hydra 0.9.9
    val exitCode = Main.compile(allArgs.toArray)

    assert(exitCode == 0, "Errors during compilation")
  }
}

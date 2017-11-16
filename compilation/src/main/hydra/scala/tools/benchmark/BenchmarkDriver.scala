package scala.tools.benchmark

import scala.tools.nsc.BaseBenchmarkDriver
import scala.tools.nsc.CompilerCommand
import scala.tools.nsc.Global
import scala.tools.nsc.reporters.Reporter

import com.triplequote.hydra.Hydra
import com.triplequote.hydra.HydraSettings
import com.triplequote.hydra.compiler.HydraGlobal
import com.triplequote.hydra.compiler.classpath.ParsedLogicalPackage

trait BenchmarkDriver extends BaseBenchmarkDriver {
  // All this code is copy-pasted from Hydra because Hydra.main calls System.exit (needed for Maven support)
  // and we don't have another entry point that's convenient to use.
  def compileImpl(): Unit = {
    val scalaSettings = new HydraSettings(Console.err.println)
    val command = new CompilerCommand(allArgs.toList, scalaSettings)

    val exitCode = Hydra.validateArguments(command) match {
      case Left(Hydra.CompilerSettingsError(msg)) =>
        scalaSettings.errorFn(msg)
        1
      case Right(sources) =>
        val cpStructure = ParsedLogicalPackage.collectLogicalPackages(scalaSettings)
        val newGlobal: Reporter => HydraGlobal = (reporter: Reporter) => new Global(scalaSettings, reporter) with HydraGlobal {
          override lazy val classPathStructure = cpStructure
        }
        val newRun = (global: HydraGlobal) => new global.Run

        Hydra.compile(sources, command, newGlobal, newRun) match {
          case Left(_) => 1
          case Right(hasErrors) => if (hasErrors) 1 else 0
        }
    }

    assert(exitCode == 0, "Errors during compilation")
  }
}

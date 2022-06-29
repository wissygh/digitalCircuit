package fifo

import java.io.File
import java.io.PrintWriter
import chisel3._
import chisel3.tester.{ChiselUtestTester, testableClock, testableData}
import utest._
import scala.util.{Random}

object FIFOTest extends TestSuite with ChiselUtestTester {
  def tests: Tests = Tests {
    test("FIFO should pass") {
//      println(chisel3.stage.ChiselStage.emitVerilog(new FIFO(8, 3)))
//      val writer = new PrintWriter(new File("/Users/chengguanghui/wissy/digitalCircuit/emitVerilog/FIFO.v" ))
//      writer.write(chisel3.stage.ChiselStage.emitVerilog(new ASYNCFIFO(8, 3)))
//      writer.close()

      val writer = new PrintWriter(new File("/Users/chengguanghui/wissy/digitalCircuit/emitVerilog/Reg.v" ))
      writer.write(chisel3.stage.ChiselStage.emitVerilog(new REGtest()))
      writer.close()
    }
  }
}
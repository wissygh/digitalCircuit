package fifo

import chisel3._
import chisel3.util.RegEnable
class REGtest extends Module{
  val io = IO(new Bundle{
    val enable = Input(Bool())
    val dout = Output(UInt(8.W))
  })
  val cNext =Wire(UInt(8.W))
  val c = RegEnable(cNext, 0.U(8.W), io.enable)
  cNext := c + 1.U

  io.dout := c
}
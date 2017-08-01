package com.tsunderebug.rendertest

import java.net.URL

import com.tsunderebug.render.{Game, RenderRequest, RenderService}

object RenderTest {

  def main(args: Array[String]): Unit = {
    RenderService.getRenderService.startAcceptance()
    RenderRequest.requestRender(new Game("portal2", "Portal 2", "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Portal 2"), new URL("https://cdn.discordapp.com/attachments/341403010695430145/341624375977574401/PitFlings_1575_Zypeh.dem"))
  }

}

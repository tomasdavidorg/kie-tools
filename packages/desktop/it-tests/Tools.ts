import { Application } from "spectron";
import { join } from "path";
import { platform } from "os";

export async function initApp(): Promise<Application> {
  return await new Application({
    path: join(__dirname, "..", "node_modules", ".bin", "electron" + (platform() === "win32" ? ".cmd" : "")),
    args: [join(__dirname, "..")]
  }).start();
}

export interface BotFile {
  name: string;
  language: string;
  content: string;
}

export interface CoordinatePoint {
  id: string;
  name: string;
  x: number;
  y: number;
  delaySec: number;
  actionType: "tap" | "swipe" | "long_press";
  endX?: number;
  endY?: number;
}

export interface BotProject {
  id: string;
  title: string;
  gameName: string;
  targetResolution: string;
  orientation: "portrait" | "landscape";
  language: "python" | "bash";
  createdAt: string;
  files: BotFile[];
  instructions: string;
  termuxQuickCommand?: string;
  notes?: string;
}

export interface GenerationOptions {
  gameName: string;
  prompt: string;
  resolution: string;
  orientation: "portrait" | "landscape";
  language: "python" | "bash";
  antiBanJitter: boolean;
  failSafeKillswitch: boolean;
  coordinateSequence: CoordinatePoint[];
}

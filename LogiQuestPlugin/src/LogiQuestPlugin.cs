namespace Loupedeck.LogiQuestPlugin
{
    using System;

    public class LogiQuestPlugin : Plugin
    {
        // No specific application requirement, works globally
        public override Boolean UsesApplicationApiOnly => false;
        public override Boolean HasNoApplication => true;

        public LogiQuestPlugin()
        {
            PluginLog.Init(this.Log);
        }

        public override void Load()
        {
            PluginLog.Info("LogiQuest Plugin Loaded!");
        }

        public override void Unload()
        {
        }
    }
}
namespace Loupedeck.LogiQuestPlugin.Commands
{
    using System;
    using System.Net.Http;
    using Loupedeck.LogiQuestPlugin; // <--- DIESE ZEILE HINZUFÜGEN

    public class BattleCommands : PluginDynamicCommand
    {
        private static readonly HttpClient client = new HttpClient();

        public BattleCommands()
            : base("Battle Action", "Triggers Hero Actions", "LogiQuest")
        {
            // Define parameters (actions) that user can map
            this.AddParameter("attack", "Hero Attack", "Battle");
            this.AddParameter("shield", "Hero Shield", "Battle");
        }

        protected override void RunCommand(String actionParameter)
        {
            PluginLog.Info($"Action Triggered: {actionParameter}");
            
            // Fire and forget request to Kotlin App
            try 
            {
                // Assumes Kotlin Ktor server is running on port 8080
                var url = $"http://127.0.0.1:8080/action/{actionParameter}";
                // Timeout added to prevent hanging if Kotlin app is closed
                client.Timeout = TimeSpan.FromSeconds(1); 
                client.GetAsync(url).ConfigureAwait(false);
            }
            catch (Exception ex) 
            {
                PluginLog.Error(ex, "Failed to talk to Kotlin App");
            }
        }

        protected override String GetCommandDisplayName(String actionParameter, PluginImageSize imageSize)
        {
            return string.IsNullOrEmpty(actionParameter) ? "Battle" : actionParameter.ToUpper();
        }
    }
}
package org.crystal.l2tmcsmanager;

import okhttp3.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crystal.link2telegram.GetUpdateEvent;
import org.crystal.link2telegram.Link2telegram;
import org.crystal.link2telegram.Link2telegramAPI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class L2TMCSManager extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            Debug();
        } catch (Throwable ignored) { }
        this.getLogger().info(Arrays.toString(Link2telegram.L2tAPI().getServerStatus()));
        this.getLogger().info("L2TMCSManager Enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("L2TMCSManager Disabled!");
    }

    @EventHandler
    private void GetUpdateListener(GetUpdateEvent event){
        this.getLogger().info(event.GetMessage());
        if(Objects.equals(event.GetMessage(), "/restart")){
            Link2telegram.L2tAPI().sendFormatedMsg("收到重启命令","Info");
            RestartServer();
        }
    }

    private void Debug() throws Throwable {
        Logger("getUpdatedText: " + Link2telegram.L2tAPI().getUpdatedText());
        Logger("getServerStatus: " + Link2telegram.L2tAPI().getServerStatus());
        Logger("getServerTPS: " + Link2telegram.L2tAPI().getServerTPS());
    }

    private void Logger(Object Log){
        this.getLogger().info(Log.toString());
    }

    private void RestartServer(){
        String Hostname = this.getConfig().getString("MCSManager.Hostname");
        String UUID = this.getConfig().getString("MCSManager.UUID");
        String REMOTE_UUID = this.getConfig().getString("MCSManager.RemoteUUID");
        String APIKEY = this.getConfig().getString("MCSManager.APIKey");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(Hostname + "/api/protected_instance/restart?uuid=" + UUID + "&remote_uuid=" + REMOTE_UUID + "&apikey=" + APIKEY)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Link2telegram.L2tAPI().sendFormatedMsg("服务器重启失败","Warn");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                if (response.isSuccessful()) {
                    Link2telegram.L2tAPI().sendFormatedMsg("服务器重启","Status");
                } else {
                    Link2telegram.L2tAPI().sendFormatedMsg("服务器重启失败","Warn");
                }
            }
        });
    }
}

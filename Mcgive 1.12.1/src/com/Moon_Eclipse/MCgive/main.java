package com.Moon_Eclipse.MCgive;

import com.Moon_eclipse.EclipseLib.LibMain;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin
  implements Listener
{
  static Configuration c;
  public static final String VAULT = "Vault";
  Economy vault = null;
  boolean vaultLoaded = false;

  public void onEnable()
  {
    saveDefaultConfig(); c = getConfig();
  }
  public void onDisable() {
  }
  public boolean onCommand(CommandSender sender, Command command, String Label, String[] args) { Player target = Bukkit.getPlayer(args[0]);
    if (sender.isOp())
    {
      if ((command.getName().equalsIgnoreCase("주기")) && (args[0].equals("리로드")))
      {
        reloadConfig();
        c = getConfig();
        sender.sendMessage("MCgive 리로드 완료했습니다. 버전: 1.1v[07.10]");
      }
      if ((command.getName().equalsIgnoreCase("주기")) && (args[0].equals("사기")))
      {
        target = Bukkit.getPlayer(args[1]);
        String ItemName = args[2];
        double price = Double.parseDouble(args[3]);

        double balance = 0.0D;
        try {
          getEconomy(); balance = Economy.getMoney(target.getName()); } catch (UserDoesNotExistException e) {
          e.printStackTrace();
        }
        if (balance - price < 0.0D)
        {
          target.sendMessage("§b[마인아레나]§e 잔액이 부족합니다.");
        }
        else
        {
          try {
            DecreaseMoney(target, price);
          } catch (NoLoanPermittedException e) {
            e.printStackTrace();
          } catch (UserDoesNotExistException e) {
            e.printStackTrace();
          }
          int amount = Integer.parseInt(args[4]);
          give(target, ItemName, amount);
        }
      }
      if ((command.getName().equalsIgnoreCase("주기")) && (args.length >= 2))
      {
        if (args.length == 3)
        {
          int amount = Integer.parseInt(args[2]);
          give(target, args[1], amount);
        }
        else
        {
          give(target, args[1], 0);
        }
      }

    }
    else
    {
      sender.sendMessage("§b[마인아레나]§e 권한이 부족합니다.");
    }
    return true;
  }

  public static String[] give(Player target, String ItemName, int amount)
  {
    Set<String> keys = c.getConfigurationSection("config").getKeys(false);
    String[] messages = new String[3];
    if ((keys == null) || (keys.isEmpty()))
    {
      System.out.println("콘피그 파일에 이상이 있습니다.");
    }
    for (String name : keys)
    {
      ArrayList<String> lore = new ArrayList<String>();
      String key = "config." + name;
      List<String> lore2 = c.getStringList(key + ".lore");

      if (!ItemName.equals(name))
        continue;
      String target_name = target.getName();
      for (int i = 0; i < lore2.size(); i++)
      {
        String loreA = ((String)lore2.get(i)).replace("&", "§");
        String loreB = loreA.replaceAll("PLAYER", target_name);
        lore.add(loreB);
      }
      String name1 = c.getString(key + ".name").replace("_", " ");
      String name2 = name1.replace("&", "§");

      String Skill_URL = c.getString(key + ".URL");
      ItemStack item = new ItemStack(0);
      if (amount == 0)
      {
        item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), c.getInt(key + ".amount"), name2, lore, c.getString(key + ".color"), c.getStringList(key + ".enchants"), Skill_URL);
      }
      else
      {
        item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), amount, name2, lore, c.getString(key + ".color"), c.getStringList(key + ".enchants"), Skill_URL);
      }

      target.getInventory().addItem(new ItemStack[] { item });

      String message = c.getString(key + ".message");
      String broadcast = c.getString(key + ".broadcast");
      String command = c.getString(key + ".command");
      if ((message != null) && (!message.equals("")))
      {
        String message2 = message.replaceAll("<playername>", target.getName());
        messages[0] = message2;
      }

      if ((broadcast != null) && (!broadcast.equals("")))
      {
        String broadcast2 = broadcast.replaceAll("<playername>", target.getName());
        messages[1] = broadcast2;
      }
      if ((command == null) || (command.equals("")))
        continue;
      String command2 = command.replaceAll("<playername>", target.getName());
      messages[2] = command2;
    }

    return messages;
  }

  public static ItemStack get_Mcgive_Item(String target, String ItemName, int amount)
  {
    Set<String> keys = c.getConfigurationSection("config").getKeys(false);
    ItemStack item = new ItemStack(0);
    String[] messages = new String[3];
    if ((keys == null) || (keys.isEmpty()))
    {
      System.out.println("콘피그 파일에 이상이 있습니다.");
    }
    for (String name : keys)
    {
      ArrayList<String> lore = new ArrayList<String>();
      String key = "config." + name;
      List<String> lore2 = c.getStringList(key + ".lore");

      if (!ItemName.equals(name))
        continue;
      for (int i = 0; i < lore2.size(); i++)
      {
        String loreA = ((String)lore2.get(i)).replace("&", "§");
        String loreB = loreA.replaceAll("PLAYER", target);
        lore.add(loreB);
      }
      String name1 = c.getString(key + ".name").replace("_", " ");
      String name2 = name1.replace("&", "§");
      String Skull_URL = c.getString(key + ".URL");

      if (amount == 0)
      {
        item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), c.getInt(key + ".amount"), name2, lore, c.getString(key + ".color"), c.getStringList(key + ".enchants"), Skull_URL);
      }
      else
      {
        item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), amount, name2, lore, c.getString(key + ".color"), c.getStringList(key + ".enchants"), Skull_URL);
      }
    }

    return item;
  }

  public static ItemStack createItem(int typeId, int metadata, int amount, String name, List<String> lore, String color, List<String> enchants, String SkullURL)
  {
    ItemStack i = new ItemStack(typeId);
    i.setDurability((short)metadata);
    i.setAmount(amount);
    ItemMeta im = i.getItemMeta();
    String ColorHex = color;
    try
    {
      if ((typeId == 298) || (typeId == 299) || (typeId == 300) || (typeId == 301))
      {
        LeatherArmorMeta im2 = (LeatherArmorMeta)im;
        im2.setColor(Color.fromRGB(Integer.parseInt(ColorHex, 16)));
      }
    } catch (Exception localException) {
    }
    im.setDisplayName(name);
    im.setLore(lore);
    i.setItemMeta(im);
    Random rnd = new Random();
    SkullMeta skull_meta;
    if (i.getType().equals(Material.SKULL))
    {
      ItemStack new_itemstack = LibMain.getSkull(SkullURL, name);
      new_itemstack.setDurability((short)metadata);
      new_itemstack.setAmount(amount);

      skull_meta = (SkullMeta)new_itemstack.getItemMeta();
      skull_meta.setLore(im.getLore());
      skull_meta.setDisplayName(name);
      new_itemstack.setItemMeta(skull_meta);
      i = new_itemstack;
    }

    if (!enchants.isEmpty())
    {
      for (String enchant : enchants)
      {
        int enchantname = Integer.parseInt(enchant.substring(0, enchant.length() - 3));
        int level = Integer.parseInt(enchant.substring(enchant.length() - 1));
        i.addUnsafeEnchantment(Enchantment.getById(enchantname), level);
      }
    }
    i = LibMain.hideFlags_Unbreak(i);
    return i;
  }

  public void DecreaseMoney(Player p, double price) throws NoLoanPermittedException, UserDoesNotExistException {
    getEconomy(); Economy.add(p.getName(), -price);
  }

  public Economy getEconomy()
  {
    if (!this.vaultLoaded)
    {
      this.vaultLoaded = true;
      if (getServer().getPluginManager().getPlugin("Vault") != null)
      {
        RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null)
        {
          this.vault = ((Economy)rsp.getProvider());
        }
      }
    }
    return this.vault;
  }
}
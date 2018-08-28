package com.Moon_Eclipse.MCgive;

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
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.Moon_eclipse.EclipseLib.LibMain;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

public class main extends JavaPlugin implements Listener {
	static Configuration c;
	public void onEnable(){this.saveDefaultConfig(); c = this.getConfig();}
	public void onDisable(){}
	public boolean onCommand(CommandSender sender , Command command , String Label, String[] args)
	{
		Player target = Bukkit.getPlayer(args[0]);
		if(sender.isOp())
		{
			//주기      사기    플레이어이름  아이템이름 가격 개수
			//주기 플레이어이름 아이템이름
			// -     0      1         2     3  4
			if(command.getName().equalsIgnoreCase("주기") && args[0].equals("리로드"))
			{
				this.reloadConfig();
				c = this.getConfig();
				sender.sendMessage("MCgive 리로드 완료했습니다. 버전: 1.1v[07.10]");
			}
			if(command.getName().equalsIgnoreCase("주기") && args[0].equals("사기"))
			{
				target = Bukkit.getPlayer(args[1]);
				String ItemName = args[2];
				double price = Double.parseDouble(args[3]);
				
				double balance = 0;
				try {
					balance = getEconomy().getMoney(target.getName());
				} catch (UserDoesNotExistException e) {e.printStackTrace();}
				
				if((balance - price) < 0)
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
			if(command.getName().equalsIgnoreCase("주기") && args.length >= 2)
			{
				give(target, args[1], 0);
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
		if (keys == null || keys.isEmpty())
		{
			System.out.println("콘피그 파일에 이상이 있습니다.");
		}
		for(String name : keys)
		{
			ArrayList<String> lore = new ArrayList<String>();
			String key = "config." + name;
			List<String> lore2 = c.getStringList(key + ".lore");
			
			if(ItemName.equals(name))
			{
				for(int i = 0; i < lore2.size() ; i++)
				{
					String loreA = lore2.get(i).replace("&", "§");
					String loreB = loreA.replaceAll("PLAYER", target.getName());
					lore.add(loreB);
				}
				String name1 = c.getString(key + ".name").replace("_", " ");
				String name2 = name1.replace("&", "§");
				ItemStack item = new ItemStack(0);
				if(amount == 0)
				{
					item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), c.getInt(key + ".amount"), name2, lore, c.getString(key + ".color"),  c.getStringList(key + ".enchants"));
				}
				else
				{
					item = createItem(c.getInt(key + ".id"), c.getInt(key + ".metadata"), amount, name2, lore, c.getString(key + ".color"),  c.getStringList(key + ".enchants"));
				}
				
				target.getInventory().addItem(item);
				
				String message = c.getString(key + ".message");
				String broadcast = c.getString(key + ".broadcast");
				String command = c.getString(key + ".command");
				if(message != null && !(message.equals("")))
				{
					String message2 = message.replaceAll("<playername>", target.getName());
					messages[0] = message2;
					
				}
				if(broadcast != null && !(broadcast.equals("")))
				{
					String broadcast2 = broadcast.replaceAll("<playername>", target.getName());
					messages[1] = broadcast2;
				}
				if(command != null && !(command.equals("")))
				{
					String command2 = command.replaceAll("<playername>", target.getName());
					messages[2] = command2;
				}
			}
		}
	return messages;
	}
	public static ItemStack createItem(int typeId, int metadata,  int amount, String name, List<String> lore, String color, List<String> enchants)
	{
		ItemStack i = new ItemStack(typeId);
		i.setDurability((short) metadata);
		i.setAmount(amount);
		ItemMeta im = i.getItemMeta();
		String ColorHex = color;
		try
		{
			if(typeId == 298 || typeId == 299 || typeId == 300 || typeId == 301)
			{
				LeatherArmorMeta im2 = (LeatherArmorMeta) im;
				im2.setColor(Color.fromRGB(Integer.parseInt(ColorHex, 16)));
			}
		}catch(Exception e){}
		im.setDisplayName(name);
		im.setLore(lore);
		i.setItemMeta(im);
		Random rnd = new Random();
		if(!(enchants.isEmpty()))
		{
			for(String enchant : enchants)
			{
				//'16: 1'
				int enchantname = Integer.parseInt(enchant.substring(0, enchant.length() - 3));
				int level = Integer.parseInt(enchant.substring(enchant.length() - 1));
				i.addUnsafeEnchantment(Enchantment.getById(enchantname), level);
			}
		}
		i = LibMain.hideFlags_Unbreak(i);
		return i;
	}
	public void DecreaseMoney(Player p, double price) throws NoLoanPermittedException, UserDoesNotExistException
	{
		getEconomy().add(p.getName(), -price);
	}
	
	public static final String VAULT = "Vault";
    Economy vault = null;
    boolean vaultLoaded = false;
	public Economy getEconomy()
	{
        if(!vaultLoaded)
        {
            vaultLoaded = true;
            if (getServer().getPluginManager().getPlugin(VAULT) != null)
            {
                RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
                if(rsp!=null)
                {
                    vault = rsp.getProvider();
                }
            }
        }
        return vault;
    }
}

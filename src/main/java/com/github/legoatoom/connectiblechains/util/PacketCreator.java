/*
 * Copyright (C) 2023 legoatoom
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.legoatoom.connectiblechains.util;

import com.github.legoatoom.connectiblechains.chain.ChainLink;
import com.github.legoatoom.connectiblechains.entity.ChainKnotEntity;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

/**
 * <a href="https://fabricmc.net/wiki/tutorial:projectiles">This class is from a tutorial</a> Edited some things to make it more useful for me.
 */
public class PacketCreator {
    /**
     * Creates a spawn packet for {@code entity} with additional data from {@code extraData}.
     *
     * @param entity    The entity to spawn
     * @param packetID  The spawn packet id
     * @param extraData Extra data supplier
     * @return A S2C packet
     */
    public static Packet<ClientCommonPacketListener> createSpawn(Entity entity, Identifier packetID, Function<PacketByteBuf, PacketByteBuf> extraData) {
        if (entity.getWorld().isClient)
            throw new IllegalStateException("Called on the logical client!");
        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(Registries.ENTITY_TYPE.getRawId(entity.getType()));
        byteBuf.writeUuid(entity.getUuid());
        byteBuf.writeVarInt(entity.getId());

        PacketBufUtil.writeVec3d(byteBuf, entity.getPos());
        // pitch and yaw don't matter so don't send them
        byteBuf = extraData.apply(byteBuf);
        return ServerPlayNetworking.createS2CPacket(packetID, byteBuf);
    }

    /**
     * Creates a multi attach packet for a knot
     *
     * @param knot the primary knot
     * @return Packet or null if no data is to be sent
     */
    public static Packet<ClientCommonPacketListener> createMultiAttach(ChainKnotEntity knot) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        List<ChainLink> links = knot.getLinks();
        IntList ids = new IntArrayList(links.size());
        IntList types = new IntArrayList(links.size());
        for (ChainLink link : links) {
            if (link.primary == knot) {
                ids.add(link.secondary.getId());
                types.add(Registries.ITEM.getRawId(link.sourceItem));
            }
        }
        if (ids.size() > 0) {
            buf.writeInt(knot.getId());
            buf.writeIntList(ids);
            buf.writeIntList(types);
            return ServerPlayNetworking.createS2CPacket(NetworkingPackets.S2C_MULTI_CHAIN_ATTACH_PACKET_ID, buf);
        }
        return null;
    }
}

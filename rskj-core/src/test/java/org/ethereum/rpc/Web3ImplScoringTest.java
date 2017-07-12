/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.ethereum.rpc;

import co.rsk.config.RskSystemProperties;
import co.rsk.core.Wallet;
import co.rsk.scoring.PeerScoringManager;
import co.rsk.scoring.PunishmentParameters;
import co.rsk.test.World;
import org.ethereum.rpc.Simples.SimpleRsk;
import org.ethereum.rpc.Simples.SimpleWorldManager;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by ajlopez on 12/07/2017.
 */
public class Web3ImplScoringTest {
    private static Random random = new Random();

    @Test
    public void addBannedAddressUsingIPV4() throws UnknownHostException {
        PeerScoringManager peerScoringManager = createPeerScoringManager();
        Web3Impl web3 = createWeb3(peerScoringManager);
        InetAddress address = generateIPAddressV6();

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));

        web3.sco_addBannedAddress(address.getHostAddress());

        Assert.assertFalse(peerScoringManager.hasGoodReputation(address));
    }

    @Test
    public void addAndRemoveBannedAddressUsingIPV4() throws UnknownHostException {
        PeerScoringManager peerScoringManager = createPeerScoringManager();
        Web3Impl web3 = createWeb3(peerScoringManager);
        InetAddress address = generateIPAddressV6();

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));

        web3.sco_addBannedAddress(address.getHostAddress());

        Assert.assertFalse(peerScoringManager.hasGoodReputation(address));

        web3.sco_removeBannedAddress(address.getHostAddress());

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));
    }

    @Test
    public void addBannedAddressUsingIPV6() throws UnknownHostException {
        PeerScoringManager peerScoringManager = createPeerScoringManager();
        Web3Impl web3 = createWeb3(peerScoringManager);
        InetAddress address = generateIPAddressV4();

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));

        web3.sco_addBannedAddress(address.getHostAddress());

        Assert.assertFalse(peerScoringManager.hasGoodReputation(address));
    }

    @Test
    public void addAndRemoveBannedAddressUsingIPV6() throws UnknownHostException {
        PeerScoringManager peerScoringManager = createPeerScoringManager();
        Web3Impl web3 = createWeb3(peerScoringManager);
        InetAddress address = generateIPAddressV4();

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));

        web3.sco_addBannedAddress(address.getHostAddress());

        Assert.assertFalse(peerScoringManager.hasGoodReputation(address));

        web3.sco_removeBannedAddress(address.getHostAddress());

        Assert.assertTrue(peerScoringManager.hasGoodReputation(address));
    }

    private static InetAddress generateIPAddressV4() throws UnknownHostException {
        byte[] bytes = new byte[4];

        random.nextBytes(bytes);

        return InetAddress.getByAddress(bytes);
    }

    private static InetAddress generateIPAddressV6() throws UnknownHostException {
        byte[] bytes = new byte[16];

        random.nextBytes(bytes);

        return InetAddress.getByAddress(bytes);
    }

    private static Web3Impl createWeb3(PeerScoringManager peerScoringManager) {
        SimpleRsk rsk = new SimpleRsk();
        rsk.setPeerScoringManager(peerScoringManager);

        World world = new World();
        SimpleWorldManager worldManager = new SimpleWorldManager();
        worldManager.setBlockchain(world.getBlockChain());
        rsk.worldManager = worldManager;

        Web3Impl web3 = new Web3Impl(rsk, RskSystemProperties.RSKCONFIG, new Wallet());

        return web3;
    }

    private static PeerScoringManager createPeerScoringManager() {
        return new PeerScoringManager(100, new PunishmentParameters(10, 10, 1000), new PunishmentParameters(10, 10, 1000));
    }
}
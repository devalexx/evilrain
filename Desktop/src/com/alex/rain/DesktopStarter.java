package com.alex.rain;/*
 *   Copyright(c) 2001-2012, Latista Technologies Inc, All Rights Reserved.
 *
 *   The software and information contained herein are copyrighted and
 *   proprietary to Latista Technologies Inc. This software is furnished
 *   pursuant to a written license agreement and may be used, copied,
 *   transmitted, and stored only in accordance with the terms of such
 *   license and with the inclusion of the above copyright notice. Please
 *   refer to the file "LICENSE" for further copyright and licensing
 *   information. This software and information or any other copies
 *   thereof may not be provided or otherwise made available to any other
 *   person.
 *
 *   LATISTA TECHNOLOGIES INC MAKES NO REPRESENTATIONS AND EXTENDS NO
 *   WARRANTIES, EXPRESS OR IMPLIED, WITH RESPECT TO THE SOFTWARE, INCLUDING,
 *   BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR ANY PARTICULAR PURPOSE, AND THE WARRANTY AGAINST INFRINGEMENT OF
 *   PATENTS OR OTHER INTELLECTUAL PROPERTY RIGHTS. THE SOFTWARE IS PROVIDED
 *   "AS IS", AND IN NO EVENT SHALL LATISTA TECHNOLOGIES INC OR ANY OF ITS
 *   AFFILIATES BE LIABLE FOR ANY DAMAGES, INCLUDING ANY LOST PROFITS OR OTHER
 *   INCIDENTAL OR CONSEQUENTIAL DAMAGES RELATING TO THE SOFTWARE.
 *
 *   Please note that this software and information are protected by copyright
 *   law and international treaties. Unauthorized use, copy and/or modification
 *   of this software and information, may result in severe civil and criminal
 *   penalties, and will be prosecuted to the maximum extent possible under the
 *   law.
 */

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;

/**
 * @author: Alexander Shubenkov
 * @since: 28.05.13
 */

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Drop";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        new LwjglApplication(RainGame.getInstance(), cfg);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}

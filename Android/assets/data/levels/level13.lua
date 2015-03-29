function onCreate()
    world:setDropsColorMixing(true)
    ------------------
    bl = luajava.new(Ground)
    bl:addVertex(42.0, 158.0)
    bl:addVertex(39.0, 39.999996)
    bl:addVertex(179.0, 39.999996)
    bl:addVertex(179.0, 158.0)
    bl:addVertex(159.00002, 159.99998)
    bl:addVertex(159.99998, 59.999996)
    bl:addVertex(57.0, 59.999996)
    bl:addVertex(58.999996, 158.0)
    bl:setPosition(0.0, 0.0)
    bl:setRotation(0.0)
    bl:setName('bl')
    bl:setVisible(true)
    world:add(bl)

    ------------------
    bm = luajava.new(Ground)
    bm:addVertex(279.0, 158.0)
    bm:addVertex(280.0, 39.999996)
    bm:addVertex(419.99997, 38.0)
    bm:addVertex(417.0, 159.99998)
    bm:addVertex(397.0, 158.0)
    bm:addVertex(399.0, 59.999996)
    bm:addVertex(300.0, 59.999996)
    bm:addVertex(299.0, 158.0)
    bm:setPosition(0.0, 0.0)
    bm:setRotation(0.0)
    bm:setName('bm')
    bm:setVisible(true)
    world:add(bm)

    ------------------
    br = luajava.new(Ground)
    br:addVertex(519.0, 158.0)
    br:addVertex(519.0, 37.0)
    br:addVertex(660.0, 39.999996)
    br:addVertex(662.0, 159.99998)
    br:addVertex(639.0, 159.99998)
    br:addVertex(637.0, 59.999996)
    br:addVertex(540.0, 59.999996)
    br:addVertex(540.0, 158.0)
    br:setPosition(0.0, 0.0)
    br:setRotation(0.0)
    br:setName('br')
    br:setVisible(true)
    world:add(br)

    ------------------
    emitter_l = luajava.new(Emitter)
    emitter_l:setPosition(228.99998, 440.99997)
    emitter_l:setRotation(-92.0)
    emitter_l:setName('emitter_l')
    emitter_l:setVisible(true)
    emitter_l:setClickable(true)
    emitter_l:setHasControl(false)
    emitter_l:setColor(luajava.new(Color, 1, 0, 0, 1))
    world:add(emitter_l)

    ------------------
    emitter_m = luajava.new(Emitter)
    emitter_m:setPosition(402.99997, 445.99994)
    emitter_m:setRotation(-91.0)
    emitter_m:setName('emitter_m')
    emitter_m:setVisible(true)
    emitter_m:setClickable(true)
    emitter_m:setHasControl(false)
    emitter_m:setColor(luajava.new(Color, 0, 1, 0, 1))
    world:add(emitter_m)

    ------------------
    emitter_r = luajava.new(Emitter)
    emitter_r:setPosition(600.0, 447.0)
    emitter_r:setRotation(-90.0)
    emitter_r:setName('emitter_r')
    emitter_r:setVisible(true)
    emitter_r:setClickable(true)
    emitter_r:setHasControl(false)
    emitter_r:setColor(luajava.new(Color, 0, 0, 1, 1))
    world:add(emitter_r)

    ------------------
    g11 = luajava.new(Ground)
    g11:addVertex(32.0, 357.0)
    g11:addVertex(39.999996, 185.0)
    g11:addVertex(53.999996, 185.0)
    g11:addVertex(44.0, 357.0)
    g11:setPosition(0.0, 0.0)
    g11:setRotation(0.0)
    g11:setName('g11')
    g11:setVisible(true)
    world:add(g11)

    ------------------
    g12 = luajava.new(Ground)
    g12:addVertex(167.0, 180.0)
    g12:addVertex(174.0, 180.0)
    g12:addVertex(733.99994, 369.99997)
    g12:addVertex(717.0, 388.0)
    g12:setPosition(0.0, 0.0)
    g12:setRotation(0.0)
    g12:setName('g12')
    g12:setVisible(true)
    world:add(g12)

    world:addInteractType(EMITTER)
end

step = 0
time = 0
function onCheck()
    if step == 0 then
        cnc = world:summaryDropsColor(50, 50, 110, 110)
        if cnc ~= nil and cnc.color.g > 0.99 and cnc.count > 40 then
            world:deleteDropsInRect(0, 170, 800, 480)
            step = 1

            g11:setPosition(-1000.0, -1000.0)
            g12:setPosition(-1000.0, -1000.0)

            g21 = luajava.new(Ground)
            g21:addVertex(29.0, 350.0)
            g21:addVertex(275.0, 165.0)
            g21:addVertex(297.0, 165.0)
            g21:addVertex(37.0, 362.0)
            g21:setPosition(0.0, 0.0)
            g21:setRotation(0.0)
            g21:setName('g21')
            g21:setVisible(true)
            world:add(g21)

            ------------------
            g22 = luajava.new(Ground)
            g22:addVertex(400.0, 168.0)
            g22:addVertex(417.0, 170.0)
            g22:addVertex(766.99994, 355.0)
            g22:addVertex(749.0, 369.99997)
            g22:setPosition(0.0, 0.0)
            g22:setRotation(0.0)
            g22:setName('g22')
            g22:setVisible(true)
            world:add(g22)
        end
    elseif step == 1 then
        cnc = world:summaryDropsColor(290, 50, 110, 110)
        if cnc ~= nil and cnc.color.r > 0.4 and cnc.color.g < 0.01 and cnc.color.b > 0.4 and cnc.count > 40 then
            world:deleteDropsInRect(0, 170, 800, 480)
            step = 2

            g21:setPosition(-1000.0, -1000.0)
            g22:setPosition(-1000.0, -1000.0)

            g31 = luajava.new(Ground)
            g31:addVertex(29.0, 350.0)
            g31:addVertex(522.0, 163.0)
            g31:addVertex(537.0, 165.0)
            g31:addVertex(35.0, 362.0)
            g31:setPosition(0.0, 0.0)
            g31:setRotation(0.0)
            g31:setName('g31')
            g31:setVisible(true)
            world:add(g31)

            ------------------
            g32 = luajava.new(Ground)
            g32:addVertex(637.0, 165.0)
            g32:addVertex(657.0, 163.0)
            g32:addVertex(772.0, 383.0)
            g32:addVertex(754.0, 387.0)
            g32:setPosition(0.0, 0.0)
            g32:setRotation(0.0)
            g32:setName('g32')
            g32:setVisible(true)
            world:add(g32)
        end
    elseif step == 2 then
        cnc = world:summaryDropsColor(530, 50, 110, 110)
        if cnc ~= nil and cnc.color.r > 0.4 and cnc.color.g > 0.4 and cnc.color.b < 0.01 and cnc.count > 40 then
            step = 3
        end
    end

    return step == 3
end

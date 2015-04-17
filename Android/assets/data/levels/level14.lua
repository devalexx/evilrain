function addObjects()
    world:setPressingAction(PICKING_BODIES);
    ------------------
    Ground_0 = luajava.new(Ground)
    Ground_0:addVertex(500.780487, 19.512192)
    Ground_0:addVertex(812.683, 21.46341)
    Ground_0:addVertex(812.683, 54.634155)
    Ground_0:addVertex(500.8292847, 54.634155)
    Ground_0:setPosition(0.0, 0.0)
    Ground_0:setRotation(0.0)
    Ground_0:setName('Ground_0')
    Ground_0:setVisible(true)
    world:add(Ground_0)

    ------------------
    Ground_1 = luajava.new(Ground)
    Ground_1:addVertex(47.804867, 200.85364)
    Ground_1:addVertex(45.85367, 66.34144)
    Ground_1:addVertex(166.82925, 66.34144)
    Ground_1:addVertex(168.78049, 200.85364)
    Ground_1:addVertex(145.36583, 200.85364)
    Ground_1:addVertex(143.41464, 87.80486)
    Ground_1:addVertex(69.26828, 83.90243)
    Ground_1:addVertex(71.21951, 200.85364)
    Ground_1:setPosition(-2.0, -8.0)
    Ground_1:setRotation(0.0)
    Ground_1:setName('Ground_1')
    Ground_1:setVisible(true)
    world:add(Ground_1)

    ------------------
    Ground_2 = luajava.new(Ground)
    Ground_2:addVertex(371.7073, 200.85364)
    Ground_2:addVertex(334.63412, 200.90242)
    Ground_2:addVertex(332.68292, 95.60973)
    Ground_2:addVertex(244.87804, 93.658516)
    Ground_2:addVertex(244.87804, 200.90242)
    Ground_2:addVertex(219.51218, 200.9512)
    Ground_2:addVertex(217.56096, 72.195114)
    Ground_2:addVertex(369.7561, 74.14633)
    Ground_2:setPosition(105.0, -15.999998)
    Ground_2:setRotation(0.0)
    Ground_2:setName('Ground_2')
    Ground_2:setVisible(true)
    world:add(Ground_2)

    ------------------
    Ground_3 = luajava.new(Ground)
    Ground_3:addVertex(521.9512, 68.292656)
    Ground_3:addVertex(717.0731, 70.24387)
    Ground_3:addVertex(715.12195, 288.7805)
    Ground_3:addVertex(687.8049, 316.09756)
    Ground_3:addVertex(664.39026, 318.04877)
    Ground_3:addVertex(660.4878, 341.46338)
    Ground_3:addVertex(660.4878, 359.02432)
    Ground_3:addVertex(672.1951, 384.3902)
    Ground_3:addVertex(681.9512, 405.8536)
    Ground_3:addVertex(668.2927, 405.8536)
    Ground_3:addVertex(648.78046, 376.58533)
    Ground_3:addVertex(648.78046, 349.26828)
    Ground_3:addVertex(650.7317, 314.1463)
    Ground_3:addVertex(685.85364, 300.4878)
    Ground_3:addVertex(695.60974, 277.07315)
    Ground_3:addVertex(695.60974, 115.12195)
    Ground_3:addVertex(693.65857, 83.90243)
    Ground_3:addVertex(541.4634, 83.90243)
    Ground_3:addVertex(541.4634, 277.07315)
    Ground_3:addVertex(549.2683, 290.7317)
    Ground_3:addVertex(564.87805, 306.34143)
    Ground_3:addVertex(578.5366, 310.24387)
    Ground_3:addVertex(578.5366, 327.80484)
    Ground_3:addVertex(582.43896, 349.26828)
    Ground_3:addVertex(584.39026, 366.8292)
    Ground_3:addVertex(576.58527, 398.04877)
    Ground_3:addVertex(562.9268, 403.90237)
    Ground_3:addVertex(562.9268, 378.53656)
    Ground_3:addVertex(570.7317, 349.26828)
    Ground_3:addVertex(555.12195, 323.9024)
    Ground_3:addVertex(529.75604, 298.53656)
    Ground_3:setPosition(0.0, -15.0)
    Ground_3:setRotation(0.0)
    Ground_3:setName('Ground_3')
    Ground_3:setVisible(true)
    Ground_3:setBodyType(DYNAMIC_BODY)
    world:add(Ground_3)

    world:addInteractType(GROUND)

    counter = 0
    for i = 0, 12 do
        for j = 1, 20 do
            local drop = luajava.new(Drop)
            drop:setPosition(luajava.new(Vector2, i * 9 + 560, j * 9 + 80))
            drop:setColor(Color.RED)
            counter = counter + 1
            world:add(drop)
        end
    end
end
function onCreate()
    addObjects()
end
function onCheck()
    if world:dropsInRect(345, 75, 100, 110) < 80 then
        return false
    end

    if world:dropsInRect(65, 73, 90, 120) < 80 then
        return false
    end

    return true
end
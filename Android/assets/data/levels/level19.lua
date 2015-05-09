function addObjects()
    world:setPressingAction(PICKING_BODIES);
    ------------------
    emitter_0 = luajava.new(Emitter)
    emitter_0:setPosition(802.0, 384.4999)
    emitter_0:setRotation(-159.0)
    emitter_0:setBodyType(KINEMATIC_BODY)
    emitter_0:setName('emitter_0')
    emitter_0:setVisible(true)
    emitter_0:setHasControl(false)
    world:add(emitter_0)

    ------------------
    Ground_1 = luajava.new(Ground)
    Ground_1:addVertex(241.94476, 256.9993)
    Ground_1:addVertex(188.94476, 241.9993)
    Ground_1:addVertex(151.94476, 209.9993)
    Ground_1:addVertex(74.94476, 198.9993)
    Ground_1:addVertex(36.944763, 139.9993)
    Ground_1:addVertex(-0.055236816, 134.9993)
    Ground_1:addVertex(1.9447632, 9.9993)
    Ground_1:addVertex(244.94476, -6.999969E-4)
    Ground_1:addToPosition(552.0, 7.0)
    Ground_1:setRotation(0.0)
    Ground_1:setBodyType(STATIC_BODY)
    Ground_1:setName('Ground_1')
    Ground_1:setVisible(true)
    world:add(Ground_1)

    ------------------
    Ground_2 = luajava.new(Ground)
    Ground_2:addVertex(-2.0003317E-4, 104.9996)
    Ground_2:addVertex(6.9997997, 9.999599)
    Ground_2:addVertex(173.9998, -4.0006635E-4)
    Ground_2:addVertex(181.99982, 131.9996)
    Ground_2:addToPosition(2.0, 4.0)
    Ground_2:setRotation(0.0)
    Ground_2:setBodyType(STATIC_BODY)
    Ground_2:setName('Ground_2')
    Ground_2:setVisible(true)
    world:add(Ground_2)

    ------------------
    Ground_3 = luajava.new(Ground)
    Ground_3:addVertex(-2.0003317E-4, 52.97238)
    Ground_3:addVertex(-2.0003317E-4, -0.027618408)
    Ground_3:addVertex(278.9998, 7.972381)
    Ground_3:addVertex(421.99976, 49.972378)
    Ground_3:addToPosition(2.0, 276.0)
    Ground_3:setRotation(0.0)
    Ground_3:setBodyType(STATIC_BODY)
    Ground_3:setName('Ground_3')
    Ground_3:setVisible(true)
    world:add(Ground_3)

    ------------------
    Ground_4 = luajava.new(Ground)
    Ground_4:addVertex(-0.029205322, 17.999598)
    Ground_4:addVertex(-0.029205322, -4.0006635E-4)
    Ground_4:addVertex(51.97079, -4.0006635E-4)
    Ground_4:addVertex(46.970795, 17.999598)
    Ground_4:addToPosition(291.99997, 4.0)
    Ground_4:setRotation(0.0)
    Ground_4:setBodyType(STATIC_BODY)
    Ground_4:setName('Ground_4')
    Ground_4:setVisible(true)
    world:add(Ground_4)

    ------------------
    Ground_5 = luajava.new(Ground)
    Ground_5:addVertex(-0.042419434, 17.999598)
    Ground_5:addVertex(-0.042419434, -4.0006635E-4)
    Ground_5:addVertex(44.95761, -4.0006635E-4)
    Ground_5:addVertex(46.957607, 17.999598)
    Ground_5:addToPosition(423.99997, 4.0)
    Ground_5:setRotation(0.0)
    Ground_5:setBodyType(STATIC_BODY)
    Ground_5:setName('Ground_5')
    Ground_5:setVisible(true)
    world:add(Ground_5)

    ------------------
    house_6 = luajava.new(House)
    house_6:setPosition(179.185, 479.6907)
    house_6:setRotation(-0.13627511)
    house_6:setBodyType(DYNAMIC_BODY)
    house_6:setName('house_6')
    house_6:setVisible(true)
    house_6:setBodyBox(32, 150)
    house_6:setSpriteBox(32, 150)
    world:add(house_6)

    ------------------
    house_7 = luajava.new(House)
    house_7:setPosition(137.43578, 480.08453)
    house_7:setRotation(-0.4200058)
    house_7:setBodyType(DYNAMIC_BODY)
    house_7:setName('house_7')
    house_7:setVisible(true)
    house_7:setBodyBox(32, 100)
    house_7:setSpriteBox(32, 100)
    world:add(house_7)

    ------------------
    house_8 = luajava.new(House)
    house_8:setPosition(231.0016, 429.09683)
    house_8:setRotation(-0.9060841)
    house_8:setBodyType(DYNAMIC_BODY)
    house_8:setName('house_8')
    house_8:setVisible(true)
    house_8:setBodyBox(45, 100)
    house_8:setSpriteBox(45, 100)
    world:add(house_8)

    ------------------
    house_9 = luajava.new(House)
    house_9:setPosition(279.43573, 479.0846)
    house_9:setRotation(-0.42006004)
    house_9:setBodyType(DYNAMIC_BODY)
    house_9:setName('house_9')
    house_9:setVisible(true)
    house_9:setBodyBox(32, 150)
    house_9:setSpriteBox(32, 150)
    world:add(house_9)

    ------------------
    house_10 = luajava.new(House)
    house_10:setPosition(327.00372, 428.32004)
    house_10:setRotation(-0.25239995)
    house_10:setBodyType(DYNAMIC_BODY)
    house_10:setName('house_10')
    house_10:setVisible(true)
    house_10:setBodyBox(45, 100)
    house_10:setSpriteBox(45, 100)
    world:add(house_10)

    world:addInteractType(HOUSE)

    time = world:getTime()
    autofire = false
end
function onCreate()
    addObjects()
end
function onCheck()
    local now = world:getTime() - time

    if autofire and now > 45 then
        emitter_0:setAutoFire(false)
        autofire = false
    end

    if not autofire and now > 35 and now <= 45 then
        emitter_0:setAutoFire(true)
        autofire = true
    end

    if world:dropsInRect(-200, 0, 200, 200) < 20 then
        return false
    end

    return true
end
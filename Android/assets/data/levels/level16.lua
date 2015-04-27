function addObjects()
    world:setPressingAction(PICKING_DROPS);

    emitter_2 = luajava.new(Emitter)
    emitter_2:setPosition(112.0, 293.99997)
    emitter_2:setRotation(-12.0)
    emitter_2:setBodyType(KINEMATIC_BODY)
    emitter_2:setName('emitter_2')
    emitter_2:setVisible(true)
    emitter_2:setHasControl(false)
    world:add(emitter_2)

    ------------------
    house_3 = luajava.new(House)
    house_3:setPosition(730.0, 189.99998)
    house_3:setRotation(0.0)
    house_3:setName('house_3')
    house_3:setVisible(true)
    world:add(house_3)

    ------------------
    Ground_4 = luajava.new(Ground)
    Ground_4:addVertex(-0.003902435, 170.9986)
    Ground_4:addVertex(19.996098, 15.998599)
    Ground_4:addVertex(309.9961, -0.0013999938)
    Ground_4:addVertex(611.9961, 12.9986)
    Ground_4:addVertex(721.9961, 19.9986)
    Ground_4:addVertex(714.9961, 74.9986)
    Ground_4:addVertex(519.9961, 75.9986)
    Ground_4:addToPosition(39.0, 14.0)
    Ground_4:setRotation(0.0)
    Ground_4:setName('Ground_4')
    Ground_4:setVisible(true)
    world:add(Ground_4)

    time = world:getTime()
    fail = false
    autofire = false
end
function onCreate()
    addObjects()
end
function onCheck()
    local now = world:getTime() - time

    if autofire == false and now > 3 then
        emitter_2:setAutoFire(true)
        autofire = true
    end

    if fail == true then
        return false
    end

    local r = house_3:getRotation()
    if r < -45 or r > 45 then
        fail = true
        return false
    end

    if now < 16 then
        return false
    end

    emitter_2:setAutoFire(false)
    return true
end
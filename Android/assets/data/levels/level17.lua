function addObjects()
    world:setPressingAction(PICKING_BODIES);
    ------------------
    Ground_0 = luajava.new(Ground)
    Ground_0:addVertex(41.55847, 417.66003)
    Ground_0:addVertex(3.052264E-9, -0.002286911)
    Ground_0:addVertex(60.259766, -0.002286911)
    Ground_0:setPosition(-3.0517578E-5, 22.857145)
    Ground_0:setRotation(0.0)
    Ground_0:setBodyType(DYNAMIC_BODY)
    Ground_0:setName('Ground_0')
    Ground_0:setVisible(true)
    world:add(Ground_0)

    ------------------
    Ground_1 = luajava.new(Ground)
    Ground_1:addVertex(2.0781193, 20)
    Ground_1:addVertex(2.07901E-4, 0)
    Ground_1:addVertex(797.92224, 0)
    Ground_1:addVertex(797.92224, 20)
    Ground_1:setPosition(-2.077942, 2.0779114)
    Ground_1:setRotation(0.0)
    Ground_1:setBodyType(STATIC_BODY)
    Ground_1:setName('Ground_1')
    Ground_1:setVisible(true)
    world:add(Ground_1)

    ------------------
    Ground_2 = luajava.new(Ground)
    Ground_2:addVertex(53.952026, 0)
    Ground_2:addVertex(22.783201, 417.66003)
    Ground_2:addVertex(-0.07397461, 0)
    Ground_2:setPosition(739.74023, 22.857145)
    Ground_2:setRotation(0.0)
    Ground_2:setBodyType(STATIC_BODY)
    Ground_2:setName('Ground_2')
    Ground_2:setVisible(true)
    world:add(Ground_2)

    ------------------
    Ground_3 = luajava.new(Ground)
    Ground_3:addVertex(-0.022033691, 0)
    Ground_3:addVertex(164.1338, 0)
    Ground_3:addVertex(162.05588, 58)
    Ground_3:addVertex(-0.022033691, 58)
    Ground_3:setPosition(325.25974, 22.77922)
    Ground_3:setRotation(0.0)
    Ground_3:setBodyType(DYNAMIC_BODY)
    Ground_3:setName('Ground_3')
    Ground_3:setVisible(true)
    world:add(Ground_3)

    ------------------
    trigger_4 = luajava.new(Trigger)
    trigger_4:setPosition(413.35168, 115.39178)
    trigger_4:setRotation(6.382442E-5)
    trigger_4:setBodyType(STATIC_BODY)
    trigger_4:setName('trigger_4')
    trigger_4:setVisible(true)
    world:add(trigger_4)

    ------------------
    emitter_5 = luajava.new(Emitter)
    emitter_5:setPosition(420.0, 427.99997)
    emitter_5:setRotation(-92.0)
    emitter_5:setBodyType(STATIC_BODY)
    emitter_5:setName('emitter_5')
    emitter_5:setVisible(true)
    emitter_5:setHasControl(false)
    world:add(emitter_5)

    world:addInteractType(GROUND)

    time = world:getTime()
    fail = false
    autofire = false
end
function onCreate()
    addObjects()
end
function onCheck()
    local now = world:getTime() - time

    if autofire == false and now > 5 then
        emitter_5:setAutoFire(true)
        world:setPressingAction(NONE);
        autofire = true
    end

    if now > 10 and trigger_4:getState() == false then
        return true
    else
        return false
    end
end
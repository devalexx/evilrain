dynActorArray = {}

function onCreate()
    local ground = luajava.new(Ground)

    ground:addVertex(624, 40)
    ground:addVertex(624, 128)
    ground:addVertex(576, 128)
    ground:addVertex(576, 88)
    ground:addVertex(496, 88)
    ground:addVertex(496, 128)
    ground:addVertex(456, 128)
    ground:addVertex(456, 88)
    ground:addVertex(384, 88)
    ground:addVertex(384, 128)
    ground:addVertex(336, 128)
    ground:addVertex(336, 88)
    ground:addVertex(264, 88)
    ground:addVertex(264, 128)
    ground:addVertex(224, 128)
    ground:addVertex(224, 88)
    ground:addVertex(152, 88)
    ground:addVertex(152, 128)
    ground:addVertex(96, 128)
    ground:addVertex(96, 40)
    world:add(ground)

    local Ground_0 = luajava.new(Ground)
    Ground_0:addVertex(335.0, 126.999985)
    Ground_0:addVertex(382.0, 124.999985)
    Ground_0:addVertex(377.0, 155.0)
    Ground_0:addVertex(397.0, 190.0)
    Ground_0:addVertex(417.0, 207.0)
    Ground_0:addVertex(444.0, 195.0)
    Ground_0:addVertex(462.0, 163.0)
    Ground_0:addVertex(455.00003, 128.0)
    Ground_0:addVertex(494.0, 126.999985)
    Ground_0:addVertex(502.0, 163.0)
    Ground_0:addVertex(502.0, 184.99998)
    Ground_0:addVertex(490.0, 214.99998)
    Ground_0:addVertex(452.0, 241.99998)
    Ground_0:addVertex(427.0, 251.99997)
    Ground_0:addVertex(366.99997, 222.0)
    Ground_0:addVertex(344.99997, 170.0)
    world:add(Ground_0)

    local cloud = luajava.new(Cloud)
    cloud:setPosition(luajava.new(Vector2, 600, 420))
    world:add(cloud)
    cloud:setMinMaxPosRectangle(0, 300, 800, 200)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 222, 160))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 332, 160))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 568, 160))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 452, 160))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    world:addInteractType(TRIGGER)
    world:setClickListener(luajava.createProxy(IEventListener, {
        handle = function (e)
            if e:getTarget() ~= nil and e:getTarget():equals(trigger) then
                trigger:setState(true)
            end
        end,
    }))
end

function onCheck()
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getState() == false then
            return false
        end
    end

    return true
end

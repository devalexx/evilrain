dynActorArray = {}

function onCreate(world)
    world:setWinHint('Push the buttons')
    world:setPressingAction(3);

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

    local cloud = luajava.new(Cloud)
    cloud:setPosition(luajava.new(Vector2, 600, 420))
    world:add(cloud)
    cloud:setMinMaxPosRectangle(0, 300, 800, 200)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 190, 128))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 300, 128))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 420, 128))
    world:add(trigger)
    table.insert(dynActorArray, trigger)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 536, 128))
    world:add(trigger)
    table.insert(dynActorArray, trigger)
end

function onCheck()
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getState() == false then
            return false
        end
    end

    return true
end

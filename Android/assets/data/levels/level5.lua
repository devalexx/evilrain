dynActorArray = {}

function onCreate(world)
    local ground = luajava.new(Ground)

    ground:addVertex(744, 32)
    ground:addVertex(736, 360)
    ground:addVertex(696, 256)
    ground:addVertex(404, 256)
    ground:addVertex(544, 224)
    ground:addVertex(688, 192)
    ground:addVertex(688, 88)
    ground:addVertex(424, 80)
    ground:addVertex(256, 64)
    ground:addVertex(88, 24)
    world:add(ground)

    local dynamicActor = luajava.new(Home)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.new(Vector2, 600, 300))
    table.insert(dynActorArray, dynamicActor)

    local emitterActor = luajava.new(Emitter)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 300, 200))
    emitterActor:setMinMaxPosRectangle(0, 150, 800, 250)
end

function onCheck()
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getRotation() > -30 and dynActorArray[i]:getRotation() < 30 then
            return false
        end
    end

    return true
end
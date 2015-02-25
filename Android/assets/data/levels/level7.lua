dynActorArray = {}

function onCreate(world)
    world:setWinHint('Put to the right basket')
    world:setPressingAction(2);

    ground = luajava.new(Ground)
    ground:addVertex(792, 0)
    ground:addVertex(792, 336)
    ground:addVertex(712, 320)
    ground:addVertex(624, 288)
    ground:addVertex(536, 248)
    ground:addVertex(432, 200)
    ground:addVertex(312, 160)
    ground:addVertex(168, 96)
    ground:addVertex(0, 0)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(0, 0)
    ground:addVertex(56, 52)
    ground:addVertex(112, 92)
    ground:addVertex(56, 200)
    ground:addVertex(0, 250)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(152, 224)
    ground:addVertex(376, 328)
    ground:addVertex(152, 272)
    ground:addVertex(0, 344)
    world:add(ground)

    emitterActor = luajava.new(Emitter)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 600, 360))
    emitterActor:setAutoFire(true)
    emitterActor:setRotation(180)
end

function onCheck(mArray)
    return false
end
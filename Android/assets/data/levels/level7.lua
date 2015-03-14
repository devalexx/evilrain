dynActorArray = {}

function onCreate(world)
    world:setWinHint('Put to the right basket')
    world:setPressingAction(2);

    local ground = luajava.new(Ground)
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

    local ground = luajava.new(Ground)
    ground:addVertex(0, 50)
    ground:addVertex(56, 60)
    ground:addVertex(112, 100)
    ground:addVertex(56, 200)
    ground:addVertex(0, 250)
    world:add(ground)

    local ground = luajava.new(Ground)
    ground:addVertex(152, 224)
    ground:addVertex(376, 328)
    ground:addVertex(152, 272)
    ground:addVertex(0, 344)
    world:add(ground)

    local emitterActor = luajava.new(Emitter)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 700, 420))
    emitterActor:setAutoFire(true)
    emitterActor:setRotation(180)
end

function onCheck()
    return false
end
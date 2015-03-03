dynActorArray = {}

function onCreate(world)
    world:setWinHint('Put to the right basket')
    world:setPressingAction(2);

    --[[ground = luajava.new(Ground)
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
    world:add(ground)]]

    ground = luajava.new(Ground)
    ground:addVertex(792, 112)
    ground:addVertex(792, 184)
    ground:addVertex(768, 184)
    ground:addVertex(768, 128)
    ground:addVertex(24, 128)
    ground:addVertex(24, 176)
    ground:addVertex(0, 176)
    ground:addVertex(0, 112)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(792, 312)
    ground:addVertex(792, 384)
    ground:addVertex(768, 384)
    ground:addVertex(768, 328)
    ground:addVertex(24, 328)
    ground:addVertex(24, 376)
    ground:addVertex(0, 376)
    ground:addVertex(0, 312)
    world:add(ground)

    emitterActor = luajava.new(Emitter)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 600, 220))
    emitterActor:setAutoFire(true)
    emitterActor:setRotation(180)

    for i = 1, 10 do
        for j = 0, 0 do
            drop = luajava.new(Drop)
            drop:setPosition(luajava.new(Vector2, i * 70, 300))
            if i <= 30 then
                drop:setColor(Color.RED)
            else
                drop:setColor(Color.BLUE)
            end
            world:add(drop)
        end
    end
end

function onCheck()
    return false
end
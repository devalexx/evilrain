function onCreate(world)
    world:setWinHint('Flood house!!')
    world:setPressingAction(1);

    ground = luajava.new(Ground)
    ground:addVertex(736, 48)
    ground:addVertex(736, 352)
    ground:addVertex(720, 352)
    ground:addVertex(712, 320)
    ground:addVertex(688, 296)
    ground:addVertex(656, 288)
    ground:addVertex(600, 272)
    ground:addVertex(568, 280)
    ground:addVertex(544, 288)
    ground:addVertex(512, 296)
    ground:addVertex(472, 280)
    ground:addVertex(440, 248)
    ground:addVertex(408, 192)
    ground:addVertex(392, 128)
    ground:addVertex(376, 72)
    ground:addVertex(72, 72)
    ground:addVertex(56, 168)
    ground:addVertex(40, 288)
    ground:addVertex(16, 344)
    ground:addVertex(8, 368)
    ground:addVertex(8, 48)
    world:add(ground)

    dynamicActor = luajava.new(Home)
    dynamicActor:setBodyBox(200, 100)
    dynamicActor:setSpriteBox(200, 100)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.new(Vector2, 200, 150))
end

function onCheck(mArray)
    count = 0
    mArray:size()
    for i = 0, mArray:size() - 1 do
        if mArray:get(i):getStringType() == "DROP" then
            if mArray:get(i):getPosition().y < 200 then
                count = count + 1
            end
        end
    end

    if count > 100 then
        return true
    else
        return false
    end
end
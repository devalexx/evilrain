dynActorArray = {}

function onCreate(world)
    world:setWinHint('Put to the right basket')
    world:setPressingAction(1);

    ground = luajava.new(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(736, 352)
    ground:addVertex(656, 352)
    ground:addVertex(648, 136)
    ground:addVertex(400, 136)
    ground:addVertex(384, 280)
    ground:addVertex(272, 280)
    ground:addVertex(256, 112)
    ground:addVertex(56, 120)
    ground:addVertex(48, 368)
    ground:addVertex(16, 368)
    ground:addVertex(8, 32)
    world:add(ground)

    for i = 10, 20 do
        for j = 10, 20 do
            drop = luajava.new(Drop)
            world:add(drop)
            drop:setPosition(luajava.new(Vector2, i * 10 + 20, j * 10 + 20))
            if i < 30 then
                drop:setColor(Color.GREEN)
            end
        end
    end
end

function onCheck(mArray)
    for i = 0, mArray:size() - 1 do
        if mArray:get(i):getStringType() == "DROP" then
            if mArray:get(i):getPosition().x < 400 then
                return false
            end
        end
    end

    return true
end
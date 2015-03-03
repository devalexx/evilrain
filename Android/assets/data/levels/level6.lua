dynActorArray = {}

function onCreate(world)
    world:setWinHint('Put to the right basket')
    world:setPressingAction(2);

    local ground = luajava.new(Ground)
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

    for i = 10, 11 do
        for j = 10, 11 do
            local drop = luajava.new(Drop)
            drop:setPosition(luajava.new(Vector2, i * 10 + 20, j * 10 + 20))
            if i < 30 then
                drop:setColor(Color.GREEN)
            end
            world:add(drop)
        end
    end
end

function onCheck(dropsPosArray, dropsCount)
    for i = 0, dropsCount - 1 do
        if Array:get(dropsPosArray, i * 2) < 400 then
            return false
        end
    end

    return true
end